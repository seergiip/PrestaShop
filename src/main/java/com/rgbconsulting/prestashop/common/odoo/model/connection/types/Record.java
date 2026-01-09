package com.rgb.training.app.common.odoo.types;

import com.rgb.training.app.common.odoo.types.Record;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Odoo Record from row.
 */
public class Record extends HashMap<Object, Object> {

    private static final long serialVersionUID = 1L;
    private static final String ODOO_DATE_FORMAT = "yyyy-MM-dd";

    public static Record fromObject(Object row) {
        Map<?, ?> map = (Map<?, ?>) row;
        return fromMap(map);
    }

    public static Record fromMap(Map<?, ?> row) {
        Record record = new Record();
        row.forEach((key, value) -> {
            record.put(key, value);
        });
        return record;
    }

    public static Record fromKeyValue(Object key, Object value) {
        Record record = new Record();
        record.put(key, value);
        return record;
    }

    public Boolean isNull(Object key) {
        Object value = get(key);
        return value == null || (value.getClass() == Boolean.class && !(Boolean) value);
    }

    public String getString(Object key) {
        return !isNull(key) ? (String) get(key) : null;
    }

    public Date getDate(Object key) {
        if (!isNull(key)) {
            try {
                return new SimpleDateFormat(ODOO_DATE_FORMAT).parse(get(key).toString());
            } catch (ParseException e) {
            }
        }
        return null;
    }

    public Double getDouble(Object key) {
        return (Double) get(key);
    }

    public Integer getInteger(Object key) {
        return (Integer) get(key);
    }

    // Many2One
    public Integer getId(Object key) {
        Object value = get(key);
        if (value.getClass() == Object[].class) {
            Object[] id = (Object[]) value;
            if (id.length == 2 && id[0] instanceof Integer) {
                return (Integer) id[0];
            }
        }
        return null; // default to null
    }

    public String getIdName(Object key) {
        Object value = get(key);
        if (value.getClass() == Object[].class) {
            Object[] id = (Object[]) value;
            if (id.length == 2 && id[1] instanceof String) {
                return (String) id[1];
            }
        }
        return null; // default null
    }

    // One2Many & Many2Many
    public Integer[] getIds(Object key) {
        Object values = get(key);
        if (values.getClass() == Object[].class) {
            return buildIds((Object[]) values);
        }
        return new Integer[]{}; // Empty list
    }

    private Integer[] buildIds(Object[] values) {
        Integer[] ids = new Integer[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof Integer) {
                ids[i] = (Integer) values[i];
            }
        }
        return ids;
    }

}
