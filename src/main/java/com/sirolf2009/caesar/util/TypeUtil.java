package com.sirolf2009.caesar.util;

import java.util.Arrays;
import java.util.List;

public class TypeUtil {

    public static List<String> numberTypes = Arrays.asList("int", "java.long.Integer", "long", "java.lang.Long", "double", "java.long.Double", "float", "java.long.Float");

    public static boolean isNumber(String type) {
        return numberTypes.contains(type);
    }
}
