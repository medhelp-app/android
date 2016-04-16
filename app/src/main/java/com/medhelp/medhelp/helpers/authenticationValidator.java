package com.medhelp.medhelp.helpers;

import com.medhelp.medhelp.exceptions.PasswordInvalidException;

import java.util.regex.Pattern;

public class authenticationValidator {

    public static boolean isValidEmail(String email) {
        Pattern emailPattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
        );

        return email != null && !email.isEmpty() && emailPattern.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) throws PasswordInvalidException {
        if (password == null || password.isEmpty() || password.length() < 4 || password.length() > 16)
            throw new PasswordInvalidException();

        return true;
    }

}
