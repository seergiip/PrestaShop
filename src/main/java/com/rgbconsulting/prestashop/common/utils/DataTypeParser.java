package com.rgbconsulting.prestashop.common.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author LuisCarlosGonzalez
 */
public class DataTypeParser {

    public static Integer objectToInteger(Object object) {
        return stringToInt(String.valueOf(object));
    }

    public static Double objectToDouble(Object object) {
        return stringToDouble(String.valueOf(object));
    }

    public static Long objectToLong(Object object) {
        return stringToLong(String.valueOf(object));
    }

    public static BigDecimal objectToBigDecimal(Object object) {
        return stringToBigDecimal(String.valueOf(object));
    }

    public static Date objectToDate(Object object, String pattern) {
        return stringToDate(String.valueOf(object), pattern);
    }

    public static String objectToString(Object object) {
        String result = null;
        result = object != null ? String.valueOf(object) : null;
        return result;
    }

    public static String objectToStringLower(Object object) {
        String result = null;
        result = object != null ? String.valueOf(object).toLowerCase() : null;
        return result;
    }

    public static Boolean objectToBoolean(Object object) {
        return Boolean.parseBoolean(String.valueOf(object));
    }

    public static Object[] objectToObjectArray(Object object) {
        if (object == null) {
            return new Object[0];
        }
        if (object instanceof Object[]) {
            return (Object[]) object;
        }
        return new Object[]{object};
    }

    /**
     * Convierte el primer elemento del <code>Array</code> a <code>Long</code>
     *
     * @param objectArray Lista de elementos
     *
     * @return Un entero si el primer valor es numérico o <code>null</code> si
     * la lista es <code>null</code> o no se puede convertir el primer elemento
     * a <code>Integer<code>
     */
    public static Integer objectArrayToInteger(Object[] objectArray) {
        if (objectArray == null) {
            return null;
        }
        return stringToInt(String.valueOf(objectArray[0]));
    }

    /**
     * Convierte el primer elemento del <code>Array</code> a <code>Long</code>
     *
     * @param objectArray Lista de elementos
     *
     * @return Un entero si el primer valor es numérico o <code>null</code> si
     * la lista es <code>null</code> o no se puede convertir el primer elemento
     * a <code>Long<code>
     */
    public static Long objectArrayToLong(Object[] objectArray) {
        if (objectArray == null || objectArray.length == 0) {
            return null;
        }
        return stringToLong(String.valueOf(objectArray[0]));
    }

    public static List<Long> objectArrayToLongArray(Object[] objectArray) {
        List<Long> results = new ArrayList<>();
        if (objectArray == null || objectArray.length == 0) {
            return null;
        }
        for (Object val : objectArray) {
            try {
                results.add(Long.parseLong(String.valueOf(val)));
            } catch (Exception e) {
            }
        }
        return results;
    }

    public static Integer stringToInt(String intStr) {
        Integer result = null;
        try {
            result = Integer.parseInt(toNotNullString(intStr));
        } catch (NumberFormatException e) {
        }
        return result;
    }

    public static Double stringToDouble(String intStr) {
        Double result = null;
        try {
            result = Double.valueOf(toNotNullString(intStr));
        } catch (NumberFormatException e) {
        }
        return result;
    }

    /**
     * Convierte una cadena a número de tipo <code>Long</code>.
     *
     * @param longStr Cadena que representa un valor numérico
     *
     * @return El resultado de la conversión o <code>null</code> si no se ha
     * podido realizar.
     */
    public static Long stringToLong(String longStr) {
        Long result = null;
        try {
            result = Long.parseLong(toNotNullString(longStr));
        } catch (NumberFormatException e) {
        }
        return result;
    }

    public static BigDecimal stringToBigDecimal(String decimalStr) {
        BigDecimal result = null;
        try {
            result = new BigDecimal(decimalStr);
        } catch (NumberFormatException e) {
        }
        return result;
    }

    public static Date stringToDate(String dateStr, String datePattern) {
        Date result = null;
        try {
            result = new SimpleDateFormat(datePattern).parse(dateStr);
        } catch (ParseException e) {
        }
        return result;
    }

    public static List<String> stringListToList(String stringList, String separator) {
        List<String> result = new ArrayList<>();
        stringList = stringList.replace("[", "").replace("]", "");
        String[] parts = new String[0];
        if (stringList != null) {
            parts = stringList.split(separator);
        }
        for (String part : parts) {
            if (part != null && !"".equals(part.trim())) {
                result.add(part.trim());
            }
        }
        if (result.isEmpty()) {
            result = null;
        }
        return result;
    }

    public static List<Long> stringListToListOfLong(String stringList, String separator) {
        List<Long> result = new ArrayList<>();
        stringList = stringList.replace("[", "").replace("]", "");
        String[] parts = new String[0];
        if (stringList != null) {
            parts = stringList.split(separator);
        }
        for (String part : parts) {
            if (part != null && !"".equals(part.trim())) {
                part = part.trim();
            }
            try {
                result.add(Long.parseLong(part));
            } catch (NumberFormatException e) {
            }
        }
        if (result.isEmpty()) {
            result = null;
        }
        return result;
    }

    public static String toNotNullString(String value) {
        return value != null ? value : "";
    }

    public static String toNotNullString(Integer value) {
        return value != null ? String.valueOf(value) : "";
    }

    public static String toNotNullString(Long value) {
        return value != null ? String.valueOf(value) : "";
    }

    public static String toNotNullString(Boolean value) {
        return value != null ? String.valueOf(value) : "false";
    }

    public static String toNotNullString(List<String> value) {
        return value != null ? Arrays.toString(value.toArray()) : "";
    }

    public static String toNotNullString(List<String> value, String separator) {
        String result = toNotNullString(value);
        result = result.replaceAll(",", separator);
        return result;
    }
}
