package com.rgbconsulting.prestashop.common.odoo.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author sergi
 */
public class Stock {

    private Integer product_id;
    private Double quantity;

    public Stock() {
        this.product_id = null;
        this.quantity = 0.0d;
    }

    public Stock(Double quantity, Integer product_id) {
        this.product_id = product_id; 
        this.quantity = quantity;
    }

    public Double getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    
    public Integer getProduct_id() {
        return this.product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public Map<String, Object> getFieldsAsHashMap() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("product_id", this.product_id);
        fields.put("quantity", this.quantity);
        return fields;
    }

    // --- Comparación de productos ---
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stock)) {
            return false;
        }
        Stock other = (Stock) o;


        return Objects.equals(this.product_id, other.product_id);
    }

    @Override
    public int hashCode() {
        if (this.product_id != null) {
            return Objects.hash(product_id);
        }
        return Objects.hash(product_id);
    }

    @Override
    public String toString() {
        return "Stock{product_id=" + product_id
                + ", quantity=" + quantity
                + "}";
    }

}
