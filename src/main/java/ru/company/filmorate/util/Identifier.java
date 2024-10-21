package ru.company.filmorate.util;

import java.util.HashMap;
import java.util.Map;

public enum Identifier {
    INSTANCE;

    private final Map<Class<?>, Integer> identifier = new HashMap<>();

    public int generate(Class<?> cl) {
        if (identifier.containsKey(cl)) {
            int id = identifier.get(cl);
            identifier.put(cl, ++id);
            return id;
        } else {
            identifier.put(cl, 1);
            return 1;
        }
    }

    public void clear(Class<?> cl) {
        identifier.remove(cl);
    }
}
