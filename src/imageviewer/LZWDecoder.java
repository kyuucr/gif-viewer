/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imageviewer;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author umum
 */
public class LZWDecoder {
    
    DataInputStream in;
    int[][] dict = new int[4096][];
    int dictlen, bitsPerCode, bitpos, size, bitsPerCodeOriginal, count, bytesnum;
    int STOP;
    int CLEAR;
    int[] table;
    int valuenow = -1;

    public LZWDecoder(int bitsPerCode, int bytesnum, int[] table, int[] wh, DataInputStream in) {
        this.in = in;
        this.bitsPerCode = bitsPerCodeOriginal = bitsPerCode;
        this.table = table;
        this.bytesnum = bytesnum;
        dictlen = table.length + 2;
        bitpos = count = 0;
        STOP = table.length + 1;
        CLEAR = table.length;
        size = wh[0] * wh[1];
        for (int i = 0; i < table.length; i++) {
            int[] temp = { i };
            dict[i] = temp;
        }
        try {
            valuenow = this.in.readUnsignedByte();
            ++count;
        } catch (IOException ex) {
            Logger.getLogger(LZWDecoder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int[] decode() throws LZWDecodeException {
        int index = 0;
        int[] pixels = new int[size];
        int cW = CLEAR;
        System.out.println("Decoding...");
        try{
        while (true) {
            int pW = cW;
            cW = nextCode();
            //System.out.println(cW);
            if (cW == -1) {
                throw new LZWDecodeException("Missed the stop code in LZWDecode!");
            }
            if (cW == STOP) {
                break;
            } else if (cW == CLEAR) {
                resetDict();
            } else if (pW == CLEAR) {
                for (int i = 0; i < dict[cW].length; i++)
                    pixels[index++] = table[dict[cW][i]];
            } else {
                if (cW < dictlen) {
                    int[] temp = new int[dict[pW].length + 1];
                    for (int i = 0; i < dict[cW].length; i++) {
                        pixels[index++] = table[dict[cW][i]];
                    }
                    System.arraycopy(dict[pW], 0, temp, 0, dict[pW].length);
                    temp[temp.length - 1] = dict[cW][0];
                    dict[dictlen++] = temp;
                } else {
                    int[] temp = new int[dict[pW].length + 1];
                    System.arraycopy(dict[pW], 0, temp, 0, dict[pW].length);
                    temp[temp.length - 1] = dict[pW][0];
                    for (int i = 0; i < temp.length; i++) {
                        pixels[index++] = table[temp[i]];
                    }
                    dict[dictlen++] = temp;
                }
                //System.out.println("Dictionary length: " + dictlen);
                if (dictlen >= (1 << bitsPerCode) && bitsPerCode < 12) {
                    bitsPerCode++;
                }
            }
        }
        } catch (NullPointerException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pixels;
    }

    private int nextCode() {
        int fillbits = 0;
        int value = 0;
        while (fillbits < bitsPerCode) {
            int nextbits = valuenow; // bitsource
            int bitsfromhere = 8 - bitpos;
            if (bitsfromhere > (bitsPerCode - fillbits)) {
                bitsfromhere = bitsPerCode - fillbits;
                nextbits &= (0xff >>> 8 - bitpos - bitsfromhere);
            }
            else nextbits >>>= bitpos;
            value |= nextbits << fillbits;
            fillbits += bitsfromhere;
            bitpos += bitsfromhere;
            if (bitpos >= 8) {
                bitpos = 0;
                try {
                    if (count >= bytesnum) {
                        bytesnum = in.readUnsignedByte();
                        count = 0;
                        //System.out.println("Bytesnum now: " + bytesnum);
                    }
                    valuenow = in.readUnsignedByte();
                    count++;
                    //System.out.println("Count read: " + valuenow + ", " + count);
                } catch (IOException ex) {
                    Logger.getLogger(LZWDecoder.class.getName()).log(Level.SEVERE, null, ex);
                    return -1;
                }
            }
        }
        return value;
    }

    private void resetDict() {
        dictlen = table.length + 2;
        bitsPerCode = bitsPerCodeOriginal;
    }

}
