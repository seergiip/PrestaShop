package com.rgbconsulting.prestashop.common.odoo.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@XmlType(name = "ProductProduct")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductProduct {

    private Integer id;
    private Integer product_tmpl_id;              
    

    public ProductProduct() { }

    public ProductProduct(Integer id, Integer product_tmpl_id) {
        this.id = id;
        this.product_tmpl_id = product_tmpl_id;
    }

    // --- Getters y Setters ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProduct_tmpl_id() { return product_tmpl_id; }
    public void setProduct_tmpl_id(Integer product_tmpl_id) { this.product_tmpl_id = product_tmpl_id; }

    // --- Convertir a mapa de campos para Odoo ---
    public Map<String, Object> getFieldsAsHashMap() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", this.id);
        fields.put("name", this.product_tmpl_id);
        return fields;
    }

    // --- Comparación de productos ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductProduct)) return false;
        ProductProduct other = (ProductProduct) o;

        // Si tiene reference, lo usamos para comparar (PrestaShop)
        if (this.product_tmpl_id != null && other.product_tmpl_id != null) {
            return this.product_tmpl_id.equals(other.product_tmpl_id);
        }

        // Sino, usamos el ID de Odoo
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        if (this.product_tmpl_id != null) return Objects.hash(product_tmpl_id);
        return Objects.hash(product_tmpl_id);
    }

    @Override
    public String toString() {
        return "Product{id=" + id
                + ", product_tmpl_id=" + product_tmpl_id
                + "}";
    }
}
