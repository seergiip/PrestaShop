package com.rgb.training.app.common.odoo.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Odoo values (values, arguments, domains, etc...)
 */
public class Values extends ArrayList<Object> {
    private static final long serialVersionUID = 1L;

    public static Values empty() {
        return new Values();
    }

    public static Values create(Object... values) {
        return empty().addValues(values);
    }

    public static Values from(Object object) {
        if (object instanceof Object[]) {
            return from((Object[]) object);
        } else {
            return empty().addValues(Arrays.asList(object).toArray());
        }
    }

    public static Values from(Object[] array) {
        return empty().addValues(array);
    }

    public Values addValues(List<Object> values) {
        addAll(values);
        return this;
    }

    public Values addValues(Object[] values) {
        addAll(Arrays.asList(values));
        return this;
    }

}
