package com.sirolf2009.caesar.util;

import com.sirolf2009.caesar.model.table.IDataPointer;

import java.util.Arrays;
import java.util.List;

public class TypeUtil {

    public static String byteType = "byte";
    public static String byteArrayType = "[B";
    public static String ByteType = "Byte";
    public static String ByteArrayType = "[Ljava.lang.Byte;";
    public static String shortType = "short";
    public static String shortArrayType = "[S";
    public static String ShortType = "java.lang.Short";
    public static String ShortArrayType = "[Ljava.lang.Short;";
    public static String intType = "int";
    public static String intArrayType = "[I";
    public static String IntType = "java.lang.Integer";
    public static String IntArrayType = "[Ljava.lang.Integer;";
    public static String longType = "long";
    public static String longArrayType = "[J";
    public static String LongType = "java.lang.Long";
    public static String LongArrayType = "[Ljava.lang.Long;";
    public static String floatType = "float";
    public static String floatArrayType = "[F";
    public static String FloatType = "java.lang.Float";
    public static String FloatArrayType = "[Ljava.lang.Float;";
    public static String doubleType = "double";
    public static String doubleArrayType = "[D";
    public static String DoubleType = "java.lang.Double";
    public static String DoubleArrayType = "[Ljava.lang.Double;";
    public static String StringType = "java.lang.String";
    public static String StringArrayType = "[Ljava.lang.String;";
    public static String DateType = "java.util.Date";
    public static String DateArrayType = "[Ljava.util.Date;";
    public static List<String> numberTypes = Arrays.asList(byteType, ByteType, shortType, ShortType, intType, IntType, longType, LongType, floatType, FloatType, doubleType, DoubleType);
    public static List<String> numberArrayTypes = Arrays.asList(byteArrayType, ByteArrayType, shortArrayType, ShortArrayType, intArrayType, IntArrayType, longArrayType, LongArrayType, floatArrayType, FloatArrayType, doubleArrayType, DoubleArrayType);

    public static boolean isNumber(IDataPointer pointer) {
        return isNumber(pointer.getType());
    }
    public static boolean isNumber(String type) {
        return numberTypes.contains(type);
    }
    public static boolean isNumberArray(IDataPointer pointer) {
        return isNumberArray(pointer.getType());
    }
    public static boolean isNumberArray(String type) {
        return numberArrayTypes.contains(type);
    }
    public static boolean isByte(IDataPointer pointer) {
        return isByte(pointer.getType());
    }
    public static boolean isByte(String type) {
        return type.equals(byteType) || type.equals(ByteType);
    }
    public static boolean isByteArray(IDataPointer pointer) {
        return isByteArray(pointer.getType());
    }
    public static boolean isByteArray(String type) {
        return type.equals(byteArrayType) || type.equals(ByteArrayType);
    }
    public static boolean isShort(IDataPointer pointer) {
        return isShort(pointer.getType());
    }
    public static boolean isShort(String type) {
        return type.equals(shortType) || type.equals(ShortType);
    }
    public static boolean isShortArray(IDataPointer pointer) {
        return isShortArray(pointer.getType());
    }
    public static boolean isShortArray(String type) {
        return type.equals(shortArrayType) || type.equals(ShortArrayType);
    }
    public static boolean isLong(IDataPointer pointer) {
        return isLong(pointer.getType());
    }
    public static boolean isLong(String type) {
        return type.equals(longType) || type.equals(LongType);
    }
    public static boolean isLongArray(IDataPointer pointer) {
        return isLongArray(pointer.getType());
    }
    public static boolean isLongArray(String type) {
        return type.equals(longArrayType) || type == LongArrayType;
    }
    public static boolean isInt(IDataPointer pointer) {
        return isInt(pointer.getType());
    }
    public static boolean isInt(String type) {
        return type.equals(intType) || type.equals(IntType);
    }
    public static boolean isIntArray(IDataPointer pointer) {
        return isIntArray(pointer.getType());
    }
    public static boolean isIntArray(String type) {
        return (type.equals(intArrayType)) || (type.equals(IntArrayType));
    }
    public static boolean isFloat(IDataPointer pointer) {
        return isFloat(pointer.getType());
    }
    public static boolean isFloat(String type) {
        return type.equals(floatType) || type.equals(FloatType);
    }
    public static boolean isFloatArray(IDataPointer pointer) {
        return isFloatArray(pointer.getType());
    }
    public static boolean isFloatArray(String type) {
        return type.equals(floatArrayType) || type.equals(FloatArrayType);
    }
    public static boolean isDouble(IDataPointer pointer) {
        return isDouble(pointer.getType());
    }
    public static boolean isDouble(String type) {
        return type.equals(doubleType) || type.equals(DoubleType);
    }
    public static boolean isDoubleArray(IDataPointer pointer) {
        return isDoubleArray(pointer.getType());
    }
    public static boolean isDoubleArray(String type) {
        return type.equals(doubleArrayType) || type.equals(DoubleArrayType);
    }
    public static boolean isString(IDataPointer pointer) {
        return isString(pointer.getType());
    }
    public static boolean isString(String type) {
        return type.equals(StringType);
    }
    public static boolean isStringArray(IDataPointer pointer) {
        return isStringArray(pointer.getType());
    }
    public static boolean isStringArray(String type) {
        return type.equals(StringArrayType);
    }
    public static boolean isDate(IDataPointer pointer) {
        return isDate(pointer.getType());
    }
    public static boolean isDate(String type) {
        return type.equals(DateType);
    }
    public static boolean isDateArray(IDataPointer pointer) {
        return isDateArray(pointer.getType());
    }
    public static boolean isDateArray(String type) {
        return type.equals(DateArrayType);
    }
}
