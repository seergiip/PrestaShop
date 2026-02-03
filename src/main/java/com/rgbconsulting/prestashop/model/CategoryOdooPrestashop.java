
package com.rgbconsulting.prestashop.model;

import java.util.Objects;

/**
 *
 * @author sergi
 */
public class CategoryOdooPrestashop {

    private Integer id;
    private Integer odoo_id;
    private Integer prestashop_id;
    private String odoo_name;
    private String prestashop_name;
    private String id_parent;
    private String active;

    public CategoryOdooPrestashop() {

    }

    public CategoryOdooPrestashop(
            Integer odoo_id, Integer prestashop_id,
            String odoo_name, String prestashop_name,
            String id_parent, String active) {
        
        this.odoo_id = odoo_id;
        this.prestashop_id = prestashop_id;
        this.odoo_name = odoo_name;
        this.prestashop_name = prestashop_name;
        this.id_parent = id_parent;
        this.active = active;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOdoo_id() {
        return this.odoo_id;
    }

    public void setOdoo_id(Integer odoo_id) {
        this.odoo_id = odoo_id;
    }

    public Integer getPrestashop_id() {
        return prestashop_id;
    }

    public void setPrestashop_id(Integer prestashop_id) {
        this.prestashop_id = prestashop_id;
    }

    public String getOdoo_name() {
        return odoo_name;
    }

    public void setOdoo_name(String odoo_name) {
        this.odoo_name = odoo_name;
    }

    public String getPrestashop_name() {
        return prestashop_name;
    }

    public void setPrestashop_name(String prestashop_name) {
        this.prestashop_name = prestashop_name;
    }

    public String getId_parent() {
        return id_parent;
    }

    public void setId_parent(String id_parent) {
        this.id_parent = id_parent;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CategoryOdooPrestashop categoryOdooPrestashop = (CategoryOdooPrestashop) o;
        return Objects.equals(id, categoryOdooPrestashop.id)
                && Objects.equals(odoo_id, categoryOdooPrestashop.odoo_id)
                && Objects.equals(prestashop_id, categoryOdooPrestashop.prestashop_id)
                && Objects.equals(odoo_name, categoryOdooPrestashop.odoo_name)
                && Objects.equals(prestashop_name, categoryOdooPrestashop.prestashop_name)
                && Objects.equals(id_parent, categoryOdooPrestashop.id_parent)
                && Objects.equals(active, categoryOdooPrestashop.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                odoo_id,
                prestashop_id,
                odoo_name,
                prestashop_name,
                id_parent,
                active
        );
    }

    // toString
    @Override
    public String toString() {
        return "TuClase{"
                + "id=" + id
                + ", odoo_id=" + odoo_id
                + ", prestashop_id=" + prestashop_id
                + ", odoo_name='" + odoo_name + '\''
                + ", prestashop_name='" + prestashop_name + '\''
                + ", id_parent='" + id_parent + '\''
                + ", active='" + active + '\''
                + '}';
    }
}
