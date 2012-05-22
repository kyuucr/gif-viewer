/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imageviewer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author umum
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            File temp = new File("animated.gif");
            ImageData test = ImageHelper.openGIF(temp);
        } catch (WrongFiletypeException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LZWDecodeException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*int test = 187;
        System.out.println(Integer.toBinaryString(test));
        int test2 = test >> 3;
        System.out.println(Integer.toBinaryString(test2));*/

    }

}
