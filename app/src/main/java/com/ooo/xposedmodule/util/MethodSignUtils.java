package com.ooo.xposedmodule.util;

public class MethodSignUtils {

    public static String getMethodSignString(Class<?> returnType, Class<?>... clazzes) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < clazzes.length; ++i) {
            sb.append(getDesc(clazzes[i]));
        }
        sb.append(")");
        if (returnType != null) {
            sb.append(getDesc(returnType));
        }else{
            sb.deleteCharAt(0);
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    private static String getDesc(final Class<?> returnType) {
        if (returnType.isPrimitive()) {
            return getPrimitiveLetter(returnType);
        }
        if (returnType.isArray()) {
            return "[" + getDesc(returnType.getComponentType());
        }
        return "L" + returnType.getName().replaceAll("\\.", "/") + ";";
    }

    private static String getPrimitiveLetter(final Class<?> type) {
        if (Integer.TYPE.equals(type)) {
            return "I";
        }
        if (Void.TYPE.equals(type)) {
            return "V";
        }
        if (Boolean.TYPE.equals(type)) {
            return "Z";
        }
        if (Character.TYPE.equals(type)) {
            return "C";
        }
        if (Byte.TYPE.equals(type)) {
            return "B";
        }
        if (Short.TYPE.equals(type)) {
            return "S";
        }
        if (Float.TYPE.equals(type)) {
            return "F";
        }
        if (Long.TYPE.equals(type)) {
            return "J";
        }
        if (Double.TYPE.equals(type)) {
            return "D";
        }
        throw new IllegalStateException("Type: " + type.getCanonicalName() + " is not a primitive type");
    }
}
