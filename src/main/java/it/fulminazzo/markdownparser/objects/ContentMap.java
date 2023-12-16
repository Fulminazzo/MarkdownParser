package it.fulminazzo.markdownparser.objects;

import java.util.LinkedHashMap;

public class ContentMap extends LinkedHashMap<String, String> {

    public ContentMap set(String key, String value) {
        if (value != null) put(key.toLowerCase(), value);
        return this;
    }

    public ContentMap set(String... objects) {
        if (objects.length > 1)
            for (int i = 0; i < objects.length; i += 2)
                set(objects[i], objects[i + 1]);
        return this;
    }
}
