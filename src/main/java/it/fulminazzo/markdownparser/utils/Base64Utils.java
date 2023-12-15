package it.fulminazzo.markdownparser.utils;

import java.util.Base64;

public class Base64Utils {

    public static String encode(String text) {
        if (text == null) return null;
        else return Base64.getEncoder().encodeToString(text.getBytes());
    }

    public static String decode(String text) {
        if (text == null) return null;
        else return new String(Base64.getDecoder().decode(text));
    }
}