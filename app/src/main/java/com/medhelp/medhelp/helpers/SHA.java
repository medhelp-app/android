package com.medhelp.medhelp.helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA {

    public static String Hash(String text) {
        String hashedPassword = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(text.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            hashedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hashedPassword;
    }

}
