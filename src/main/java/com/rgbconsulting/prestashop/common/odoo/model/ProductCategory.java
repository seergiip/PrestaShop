package com.rgbconsulting.prestashop.common.odoo.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@XmlType(name = "ProductCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductCategory {

    private Integer id;                       // ID de Odoo
    private String name;              // Nombre del producto
    private String complete_name; 
    private Integer parent_id;       // Precio de venta (Odoo list price)


    public ProductCategory() { }

    public ProductCategory(Integer id, String name, String complete_name, Integer parent_id) {
        this.id = id;
        this.name = name;
        this.complete_name = complete_name;
        this.parent_id = parent_id;
    }

    // --- Getters y Setters ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getParent_Id() { return parent_id; }
    public void setParent_Id(Integer id) { this.parent_id = parent_id; }

    public String getComplete_Name() { return complete_name; }
    public void setComplete_Name(String name) { this.complete_name = complete_name; }

    
    // --- Convertir a mapa de campos para Odoo ---
    public Map<String, Object> getFieldsAsHashMap() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", this.id);
        fields.put("name", this.name);
        fields.put("complete_name", this.complete_name);
        fields.put("parent_id", this.parent_id);
        return fields;
    }

    // --- Comparación de productos ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCategory)) return false;
        ProductCategory other = (ProductCategory) o;

        // Si tiene reference, lo usamos para comparar (PrestaShop)
        if (this.name != null && other.name != null) {
            return this.name.equals(other.name);
        } else {
            return false;
        }
        
    }

    @Override
    public int hashCode() {
        if (this.name != null) return Objects.hash(this.name);
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Product{id=" + this.id
                + ", name=" + this.name
                + ", complete_name=" + this.complete_name
                + ", parent_id=" + this.parent_id + "}";
    }
}
