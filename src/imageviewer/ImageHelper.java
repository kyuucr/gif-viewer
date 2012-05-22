/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imageviewer;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author umum
 */
public class ImageHelper {

    public ImageHelper() {

    }

    public static ImageData openGIF(File file) throws WrongFiletypeException, IOException, LZWDecodeException {
        ImageData out = new ImageData();
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        //NOTE: sortTable, maxResolution, bgIndex and pixelRatio is just stored, still don't know what to do with it.
        boolean useGlobalTable = false;
        boolean sortTable = false;
        int maxResolution = 1;
        int tableSize = 0;
        int bgIndex = -1;
        int pixelRatio = 0;
        int[] table = new int[0];

        try {
            //read the header
            byte[] temp = new byte[6];
            in.read(temp);
            String header = new String(temp);
            if (!header.equals("GIF87a") && !header.equals("GIF89a")) {
                throw new WrongFiletypeException();
            }
            out.setWidth(in.readUnsignedByte() + (in.readUnsignedByte() * 256));
            out.setHeight(in.readUnsignedByte() + (in.readUnsignedByte() * 256));
            int temp2 = in.readUnsignedByte();
            //NOTE: print it to full 8 bit binary, for checking
            //System.out.println(toBinary(field));
            if ((temp2 & 128) == 128) {
                useGlobalTable = true;
            }
            maxResolution = (temp2 & 112) >> 4;
            if ((temp2 & 8) == 8) {
                sortTable = true;
            }
            tableSize = (int) Math.pow(2, (temp2 & 7) + 1);
            if (useGlobalTable) {
                bgIndex = in.readUnsignedByte();

            } else {
                in.skipBytes(1);

            }
            pixelRatio = in.readUnsignedByte();
            table = new int[tableSize];
            if (useGlobalTable) {
                System.out.println("Reading global color table...");
                for (int i = 0; i < tableSize; i++) {
                    int red = in.readUnsignedByte();
                    int green = in.readUnsignedByte();
                    int blue = in.readUnsignedByte();
                    System.out.println("#" + i + " " + red + " " + green + " " + blue);
                    table[i] = (255 << 24) | (red << 16) | (green << 8) | blue;
                }
            }
            int id;
            while ((id = in.readUnsignedByte()) != 59) {
                boolean useTransparent = false;
                int transparentIndex = 0;
                if (id == 33) {
                    //skip all the extensions except GCE
                    int ext = in.readUnsignedByte();
                    if (ext == 249) {
                        in.skipBytes(1);
                        if ((in.readUnsignedByte() & 1) == 1)
                            useTransparent = true;
                        out.setAnimationDelay(in.readUnsignedByte() + in.readUnsignedByte() * 256);
                        if (useTransparent) {
                            transparentIndex = in.readUnsignedByte();
                            table[transparentIndex] &= (0 << 24);
                        }
                        else in.skip(1);
                    }
                    else {
                        System.out.println("Skipping " + Integer.toHexString(ext) + " extension sector ...");
                        in.skipBytes(in.readUnsignedByte());
                    }
                    while (in.readUnsignedByte() != 0) {
                        in.skipBytes(1);
                    }
                } else if (id == 44) {
                    //this sector is image
                    //TODO: write blog about this shit, rly
                    int[] tempTable = new int[0];
                    int start[] = {in.readUnsignedByte() + (in.readUnsignedByte() * 256), in.readUnsignedByte() + (in.readUnsignedByte() * 256)};
                    int wh[] = {in.readUnsignedByte() + (in.readUnsignedByte() * 256), in.readUnsignedByte() + (in.readUnsignedByte() * 256)};
                    temp2 = in.readUnsignedByte();
                    if ((temp2 & 128) == 128) {
                        tempTable = table;      //backup Global Color Table
                        int localTableSize = temp2 & 7;
                        localTableSize = (int) Math.pow(2, localTableSize + 1);
                        table = new int[localTableSize];
                        System.out.println("Reading local table...");
                        for (int i = 0; i < localTableSize; i++) {
                            int red = in.readUnsignedByte();
                            int green = in.readUnsignedByte();
                            int blue = in.readUnsignedByte();
                            System.out.println("#" + i + " " + red + " " + green + " " + blue);
                            table[i] = (255 << 24) | (red << 16) | (green << 8) | blue;
                        }
                    }
                    int bitsPerCode = in.readUnsignedByte() + 1;
                    int bytes = in.readUnsignedByte();
                    LZWDecoder dec = new LZWDecoder(bitsPerCode, bytes, table, wh, in);
                    out.setPixels(dec.decode());
                    out.setStartPoint(start);
                    if ((temp2 & 128) == 128) {
                        table = tempTable;
                    }
                    if (useTransparent) {
                        table[transparentIndex] |= (255 << 24);
                        useTransparent = false;
                        transparentIndex = 0;
                    }
                    while (in.readUnsignedByte() != 0) {
                        in.skipBytes(1);
                    }
                }
            }
        } catch (EOFException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("\n\n\n===========\nImage data\n===========\nWidth: " + out.getWidth() + 
                "\nHeight: " + out.getHeight() + "\nUse Global Table: " + useGlobalTable + 
                "\nMax Resolution Bit: 3 x " + maxResolution + " bit\nSort Table: " + sortTable + 
                "\nTable Size: " + tableSize + "\nBackground Color Index: " + bgIndex + 
                "\nPixel Ratio: " + pixelRatio + "\nGlobal Color Table:");
        for (int i = 0; i < tableSize; i++) {
            System.out.println("#" + i + " " + Integer.toBinaryString(table[i]));
        }

        return out;
    }

    private static String toBinary(int input) {
        String out = Integer.toBinaryString(input);
        String temp = "";
        int dif = 8 - out.length();
        while (dif > 0) {
            temp += "0";
            dif --;
        }
        out = temp + out;
        return out;
    }
}
