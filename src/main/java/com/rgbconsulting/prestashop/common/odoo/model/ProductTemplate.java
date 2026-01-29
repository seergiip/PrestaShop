package com.rgbconsulting.prestashop.common.odoo.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@XmlType(name = "ProductTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductTemplate {

    private Integer id;                       // ID de Odoo
    private String name;              // Nombre del producto
    private Double sales_price;       // Precio de venta (Odoo list price)
    private Double cost;              // Costo (Odoo standard price)

    private String reference;                 // Referencia PrestaShop
    private Integer prestashopId;     // ID del producto en PrestaShop (opcional)
    private String categ_id;          // Category ID
    private String imageFile;

    public ProductTemplate() {
    }

    public ProductTemplate(Integer id, String name, Double sales_price, Double cost, String reference, String categ_id) {
        this.id = id;
        this.name = name;
        this.sales_price = sales_price;
        this.cost = cost;
        this.reference = reference;
        this.categ_id = categ_id;
    }

    // --- Getters y Setters ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSales_price() {
        return sales_price;
    }

    public void setSales_price(Double sales_price) {
        this.sales_price = sales_price;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getPrestashopId() {
        return prestashopId;
    }

    public void setPrestashopId(Integer prestashopId) {
        this.prestashopId = prestashopId;
    }

    public String getCategId() {
        return this.categ_id;
    }

    public void setCategId(String categ_id) {
        this.categ_id = categ_id;
    }

    public String getImageFile() {
        if (this.id == null) {
            return null;
        }
        return "http://localhost:8069/web/image?model=product.template&id="
                + this.id + "&field=image_128";
    }

    // --- Convertir a mapa de campos para Odoo ---
    public Map<String, Object> getFieldsAsHashMap() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", this.id);
        fields.put("name", this.name);
        fields.put("list_price", this.sales_price);
        fields.put("standard_price", this.cost);
        fields.put("default_code", this.reference);
        fields.put("categ_id", this.categ_id);
        return fields;
    }

    // --- Comparación de productos ---
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductTemplate)) {
            return false;
        }
        ProductTemplate other = (ProductTemplate) o;

        // Si tiene reference, lo usamos para comparar (PrestaShop)
        if (this.reference != null && other.reference != null) {
            return this.reference.equals(other.reference);
        }

        // Sino, usamos el ID de Odoo
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        if (this.reference != null) {
            return Objects.hash(reference);
        }
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{id=" + id
                + ", prestashopId=" + prestashopId
                + ", reference=" + reference
                + ", name=" + name
                + ", sales_price=" + sales_price
                + ", cost=" + cost
                + ", categ_id=" + categ_id + "}";
    }
}
