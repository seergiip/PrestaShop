package com.rgbconsulting.prestashop.common.odoo.types;

/**
 * Odoo domain arguments
 */
public class Domain extends Values {
    private static final long serialVersionUID = 1L;

    public static Domain create() {
        return new Domain();
    }

    // common filters

    public Domain op(String operator) {
        add(operator);
        return this;
    }

    public Domain filter(String field, String operator, Object value) {
        add(create(field, operator, value));
        return this;
    }

    // logical operators

    public Domain op_AND() {
        return this.op("&");
    }

    public Domain op_OR() {
        return this.op("|");
    }

    public Domain op_NOT() {
        return this.op("!");
    }

    // extra filters ...

}
