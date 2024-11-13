package ru.company.filmorate.util;

import java.util.HashMap;
import java.util.Map;

public enum Identifier {
    INSTANCE;

    private final Map<Class<?>, Long> identifier = new HashMap<>();

    public long generate(Class<?> cl) {
        if (identifier.containsKey(cl)) {
            long id = identifier.get(cl);
            identifier.put(cl, ++id);
            return id;
        } else {
            identifier.put(cl, 1L);
            return 1;
        }
    }

    public void clear(Class<?> cl) {
        identifier.remove(cl);
    }
}
