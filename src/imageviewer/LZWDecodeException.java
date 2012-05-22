/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imageviewer;

/**
 *
 * @author umum
 */
public class LZWDecodeException  extends Exception {

    private String error;

    public LZWDecodeException(){
        super();
        error = "LZW Decoder Exception!!";
    }

    public LZWDecodeException(String err){
        super(err);
        error = err;
    }

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

}
