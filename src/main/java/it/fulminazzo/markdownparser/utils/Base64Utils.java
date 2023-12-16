package it.fulminazzo.markdownparser.utils;

import java.util.Base64;

public class Base64Utils {

    public static String encode(String text) {
        if (text == null) return null;
        return Base64.getUrlEncoder().encodeToString(text.getBytes());
    }

    public static String decode(String text) {
        if (text == null) return null;
        return new String(Base64.getUrlDecoder().decode(text));
    }
}