package it.fulminazzo.markdownparser.utils;

import java.util.Date;
import java.util.concurrent.Callable;

public class ProgramUtils {

    public static <T> T executeTimed(Callable<T> function, String text) {
        try {
            Date date = new Date();
            T t = function.call();
            System.out.println(text.replace("%time%", String.valueOf(getTimeInSeconds(date))));
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static double getTimeInSeconds(Date date) {
        if (date == null) return -1;
        return ((double) new Date().getTime() - date.getTime()) / 1000;
    }
}