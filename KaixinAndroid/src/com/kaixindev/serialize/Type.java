package com.kaixindev.serialize;

public class Type {
    public static final String BOOLEAN = "Boolean";
    public static final String BYTE = "Byte";
    public static final String SHORT = "Short";
    public static final String INTEGER = "Integer";
    public static final String LONG = "Long";
    public static final String FLOAT = "Float";
    public static final String DOUBLE = "Double";
    
    public static final String STRING = "String";
    
    public static final String BOOLEAN_SHORT = "Z";
    public static final String BYTE_SHORT = "B";
    public static final String SHORT_SHORT = "S";
    public static final String INTEGER_SHORT = "I";
    public static final String LONG_SHORT = "J";
    public static final String FLOAT_SHORT = "F";
    public static final String DOUBLE_SHORT = "D";
    
    public static final String ARRAY_PREFIX = "[";
    public static final String ARRAY_SUFFIX = "[]";
    private static final String PRIMITIVE_TYPE_PREFIX = "java.lang.";
    
    /**
     * Get short name of type.
     * @param type Long type name.
     * @return
     */
    public static String javaTypeToName(String type) {
        if (type.startsWith(ARRAY_PREFIX)) {
            String testType = type;
            String suffix = "";
            while (testType != null && testType.length() > 0 && testType.startsWith(ARRAY_PREFIX)) {
                testType = testType.substring(1);
                suffix += "[]";
            }
            if (testType == null) {
                return null;
            } else if (testType.equals(BOOLEAN_SHORT)) {
                testType = BOOLEAN;
            } else if (testType.equals(BYTE_SHORT)) {
                testType = BYTE;
            } else if (testType.equals(SHORT_SHORT)) {
                testType = SHORT;
            } else if (testType.equals(INTEGER_SHORT)) {
                testType = INTEGER;
            } else if (testType.equals(LONG_SHORT)) {
                testType = LONG;
            } else if (testType.equals(FLOAT_SHORT)) {
                testType = FLOAT;
            } else if (testType.equals(DOUBLE_SHORT)) {
                testType = DOUBLE;
            }
            return getShortTypeName(testType) + suffix;
        } else {
            return getShortTypeName(type);
        }
    }
    
    public static String getShortTypeName(String name) {
        if (name.startsWith("L") && name.endsWith(";")) {
            name = name.substring(1,name.length()-1);
        }
        if (name.startsWith(PRIMITIVE_TYPE_PREFIX)) {
            String testType = name.substring(PRIMITIVE_TYPE_PREFIX.length());
            if (isPrimitive(testType)) {
                name = testType;
            }
        }
        return name;
    }
    
    /**
     * Get long name of type.
     * @param type Short type name.
     * @return
     */
    public static String toJavaName(String type) {
        if (isPrimitive(type)) 
        {
            return "java.lang." + type;
        } else {
            return type;
        }
    }
    
    /**
     * Check if it is a array type.
     * @param type
     * @return
     */
    public static boolean isArray(String type) {
        return type.endsWith(ARRAY_SUFFIX);
    }
    
    /**
     * Get type of a array.
     * @param type
     * @return
     */
    public static String getArrayType(String type) {
        if (isArray(type)) {
            return type.substring(0,type.length() - ARRAY_SUFFIX.length());
        } else {
            return type;
        }
    }
    
    /**
     * Check if is primitive type.
     * @param type
     * @return
     */
    public static boolean isPrimitive(String type) {
        return (type.equals(Type.BOOLEAN)
                || type.equals(Type.BYTE)
                || type.equals(Type.DOUBLE)
                || type.equals(Type.FLOAT)
                || type.equals(Type.INTEGER)
                || type.equals(Type.LONG)
                || type.equals(Type.SHORT)
                || type.equals(Type.STRING));
    }
    
    public static boolean isSerializable(String type) {
        try {
            Class<?> klass = Class.forName(type);
            Class<?>[] interfaces = klass.getInterfaces();
            for (Class<?> i : interfaces) {
                String name = i.getName();
                if (name.equals(Serializable.class.getName())) {
                    return true;
                }
            }
            return false;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}
