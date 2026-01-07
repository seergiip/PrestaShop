package com.rgbconsulting.prestashop.common.odoo.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Odoo Recordset (local).
 */
public class Recordset extends ArrayList<Record> {
    private static final long serialVersionUID = 1L;

    public static Recordset empty() {
        return new Recordset();
    }

    public static Recordset create(List<Object> rows) {
        Recordset set = empty();
        rows.forEach((row) -> {
            set.add(Record.fromObject(row));
        });
        return set;
    }

    public Record find(String key, Object value) {
        for(Record record: this) {
            if(record.get(key).equals(value)) {
                return record;
            }
        }
        return null;
    }

}
