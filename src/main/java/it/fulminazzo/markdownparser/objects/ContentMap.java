package it.fulminazzo.markdownparser.objects;

import java.util.LinkedHashMap;

/**
 * An implementation of {@link LinkedHashMap}
 * for displaying a node contents.
 */
public class ContentMap extends LinkedHashMap<String, String> {

    /**
     * Set content map.
     *
     * @param key   the key
     * @param value the value
     * @return the content map
     */
    public ContentMap set(String key, String value) {
        if (value != null) put(key.toLowerCase(), value);
        return this;
    }

    /**
     * Set content map.
     *
     * @param objects the objects
     * @return the content map
     */
    public ContentMap set(String... objects) {
        if (objects.length > 1)
            for (int i = 0; i < objects.length; i += 2)
                set(objects[i], objects[i + 1]);
        return this;
    }
}
