package co.com.crediya.model.user.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String email){
        super("Correo electronico ya registrado");
    }
}
