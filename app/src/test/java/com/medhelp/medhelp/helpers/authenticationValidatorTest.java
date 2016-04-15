package com.medhelp.medhelp.helpers;

import com.medhelp.medhelp.exceptions.PasswordInvalidException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class authenticationValidatorTest {

    @Test
    public void isValidEmail_CorrectEmail_ReturnsTrue() throws Exception {
        assertEquals(authenticationValidator.isValidEmail("valid@email.com"), true);
        assertEquals(authenticationValidator.isValidEmail("valid@email.com.br"), true);
    }

    @Test
    public void isValidEmail_IncorrectEmail_ReturnsFalse() throws Exception {
        assertEquals(authenticationValidator.isValidEmail("invalid"), false);
        assertEquals(authenticationValidator.isValidEmail("invalid@email"), false);
    }

    @Test
    public void isValidEmail_NullEmail_ReturnsFalse() throws Exception {
        assertEquals(authenticationValidator.isValidEmail(null), false);
    }

    @Test
    public void isPasswordValid_CorrectPassword_ReturnsTrue() throws Exception {
        assertEquals(authenticationValidator.isValidPassword("123456"), true);
    }

    @Test(expected = PasswordInvalidException.class)
    public void isPasswordValid_NullPassword_ThrowsException() throws Exception {
        assertEquals(authenticationValidator.isValidPassword(null), false);
    }

    @Test(expected = PasswordInvalidException.class)
    public void isPasswordValid_InvalidPassword_ThrowsException() throws Exception {
        authenticationValidator.isValidPassword("123");
    }

}