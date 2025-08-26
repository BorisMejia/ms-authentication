package co.com.crediya.model.user.exception;

public class EmailAlreadyExists extends RuntimeException{
    public EmailAlreadyExists(String email){
        super("Correo electronico ya registrado");
    }
}
