/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imageviewer;

/**
 *
 * @author umum
 */
public class WrongFiletypeException extends Exception {

    private String error;

    public WrongFiletypeException(){
        super();
        error = "Wrong filetype!!";
    }

    public WrongFiletypeException(String err){
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
