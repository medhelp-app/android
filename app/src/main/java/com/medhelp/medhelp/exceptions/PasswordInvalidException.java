package com.medhelp.medhelp.exceptions;

public class PasswordInvalidException extends Exception {

    public static final String PASSWORD_INVALID = "A senha deve conter de 4 a 16 caracteres";

    public PasswordInvalidException(){
        super(PASSWORD_INVALID);
    }

}
