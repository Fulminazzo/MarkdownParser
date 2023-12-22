package it.fulminazzo.markdownparser.utils;

import java.util.Base64;

/**
 * A series of Base64 utility functions.
 */
public class Base64Utils {

    /**
     * Encode a string in Base64.
     *
     * @param text the text
     * @return the Base64 string
     */
    public static String encode(String text) {
        if (text == null) return null;
        return Base64.getUrlEncoder().encodeToString(text.getBytes());
    }

    /**
     * Decode a string from Base64.
     *
     * @param text the Base64 string
     * @return the text
     */
    public static String decode(String text) {
        if (text == null) return null;
        return new String(Base64.getUrlDecoder().decode(text));
    }
}