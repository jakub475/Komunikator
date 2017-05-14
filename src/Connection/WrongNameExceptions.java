package Connection;

/**
 * Created by Kuba on 29.01.2017.
 */
public class WrongNameExceptions extends Exception {

    private String message;

    WrongNameExceptions(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
