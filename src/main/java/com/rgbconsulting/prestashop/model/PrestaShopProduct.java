package com.rgbconsulting.prestashop.model;

/**
 *
 * @author sergi
 */
public class PrestaShopProduct {

    private int id;
    private String image;
    private String name;
    private String reference;
    private String category;
    private float priceImpExcl;
    private float priceImpIncl;
    private int quantity;
    private boolean status;

    public PrestaShopProduct() {
        this.id = 0;
        this.image = "";
        this.name = "";
        this.reference = "";
        this.category = "";
        this.priceImpExcl = 0.0f;
        this.priceImpIncl = 0.0f;
        this.quantity = 0;
        this.status = false;
    }

    public PrestaShopProduct(int id, String image, String name, String reference,
            String category, float priceImpExcl, float priceImpIncl,
            int quantity, boolean status) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.reference = reference;
        this.category = category;
        this.priceImpExcl = priceImpExcl;
        this.priceImpIncl = priceImpIncl;
        this.quantity = quantity;
        this.status = status;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getPriceImpExcl() {
        return this.priceImpExcl;
    }

    public void setPriceImpExcl(float priceImpExcl) {
        this.priceImpExcl = priceImpExcl;
    }

    public float getPriceImpIncl() {
        return this.priceImpIncl;
    }

    public void setPriceImpIncl(float priceImpIncl) {
        this.priceImpIncl = priceImpIncl;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
