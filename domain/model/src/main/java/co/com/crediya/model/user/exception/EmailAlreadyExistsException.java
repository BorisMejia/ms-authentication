package co.com.crediya.model.user.exception;

public class EmailAlreadyExistsException extends DomainException{
    public EmailAlreadyExistsException(String email){
        super("Correo electronico no permitido");
    }

    public EmailAlreadyExistsException(String message){
        super(message);
    }
}
