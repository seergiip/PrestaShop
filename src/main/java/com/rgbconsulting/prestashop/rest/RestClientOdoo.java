package com.rgbconsulting.prestashop.rest;

import com.rgb.training.app.common.odoo.types.Recordset;
import com.rgb.training.app.common.odoo.types.Values;
import com.rgbconsulting.prestashop.common.odoo.model.ProductTemplate;
import com.rgbconsulting.prestashop.common.odoo.model.Stock;
import com.rgbconsulting.prestashop.common.odoo.model.connection.OdooConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import com.rgb.training.app.common.odoo.types.Record;
import com.rgbconsulting.prestashop.common.odoo.model.ProductCategory;
import com.rgbconsulting.prestashop.common.odoo.model.ProductProduct;
import com.rgbconsulting.prestashop.model.CategoryOdooPrestashop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author sergi
 */
public class RestClientOdoo {

    private static final String URL = "http://localhost:8069/";
    private static String db = "odoo16";
    private static String uid = "2";
    private static String pwd = "admin";
    private OdooConnection oc;

    public List<ProductTemplate> getProductsTemplate() throws Exception {
        Recordset rc;
        List<ProductTemplate> products = new ArrayList();
        ProductTemplate product;

        try {
            rc = odoo().search_read("product.template");
            Record record;
            for (int i = 0; i < rc.size(); i++) {
                product = new ProductTemplate();
                record = rc.get(i);
                product.setId((Integer) record.get("id"));
                product.setName((String) record.get("name"));
                product.setSales_price((Double) record.get("list_price"));
                product.setCost((Double) record.get("standard_price"));
                Object[] categ = (Object[]) record.get("categ_id");
                String categorys = (String) categ[1];
                String categoryName[] = categorys.split("/");
                product.setCategId(categoryName[categoryName.length - 1].toString());

                if (record.get("default_code") instanceof String) {
                    product.setReference((String) record.get("default_code"));
                } else {
                    product.setReference(null);
                }
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<ProductProduct> getProductsProduct() throws Exception {
        Recordset rc;
        List<ProductProduct> products = new ArrayList();
        ProductProduct product;

        try {
            rc = odoo().search_read("product.product");
            Record record;
            for (int i = 0; i < rc.size(); i++) {
                product = new ProductProduct();
                record = rc.get(i);
                product.setId((Integer) record.get("id"));
                product.setProduct_tmpl_id((Integer) record.getId("product_tmpl_id"));
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public Integer createProduct(ProductTemplate product) throws Exception {
        int productId = 0;

        try {
            Values fields = Values.create(product.getFieldsAsHashMap());
            productId = odoo().create("product.template", fields);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
        return productId;
    }

    public Boolean updateProduct(ProductTemplate product) throws Exception {
        Boolean result = false;

        try {
            Values ids = Values.create(product.getId().intValue());
            Values fields = Values.create(ids, product.getFieldsAsHashMap());
            result = odoo().update("product.template", fields);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
        return result;
    }

    public Recordset deleteProduct(ProductTemplate product) throws Exception {
        Recordset deleted = null;
        try {
            // Crear Values con el ID del cliente
            Values ids = Values.create(product.getId());

            // Ejecutar el borrado en Odoo
            deleted = odoo().delete("product.template", ids);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
        return deleted;
    }

    public List<Stock> getStocks() throws Exception {
        Recordset rc;
        List<Stock> stocks = new ArrayList();
        Stock stock;

        try {
            rc = odoo().search_read("stock.quant");
            Record record;
            for (int i = 0; i < rc.size(); i++) {
                stock = new Stock();
                record = rc.get(i);
                Object[] product = (Object[]) record.get("product_id");
                Integer productId = (Integer) product[0];
                stock.setProduct_id(productId);
                stock.setQuantity((Double) record.get("quantity"));
                stocks.add(stock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stocks;
    }

    public Double getQuantity(Integer product_id, List<Stock> stocks) {
        Stock stock;
        Double quantity = 0.0d;

        for (int i = 0; i < stocks.size(); i++) {
            stock = stocks.get(i);
            if (stock.getProduct_id().equals(product_id)) {
                quantity = stock.getQuantity();
                break;
            }
        }

        return quantity;
    }

    public List<ProductCategory> getProductsCategory() throws Exception {
        Recordset rc;
        List<ProductCategory> products = new ArrayList();
        ProductCategory product;

        try {
            rc = odoo().search_read("product.category");
            Record record;
            for (int i = 0; i < rc.size(); i++) {
                product = new ProductCategory();
                record = rc.get(i);
                product.setId((Integer) record.get("id"));
                product.setParent_Id((Integer) record.get("parent_id"));
                product.setComplete_Name((String) record.get("complete_name"));
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public File downloadImage(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        File tempFile = File.createTempFile("odoo_image_", ".jpg");

        try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }

    synchronized OdooConnection odoo() throws Exception {
        if (this.oc == null) {
            try {
                this.oc = new OdooConnection(URL, db, uid, pwd);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return this.oc;
    }

    public List<CategoryOdooPrestashop> getAllCategoriesOdooPrestashop() throws Exception {
        List<CategoryOdooPrestashop> categories = new ArrayList();
        CategoryOdooPrestashop category = new CategoryOdooPrestashop();
        String sql = "SELECT * FROM category_odoo_prestashop";

        try (Connection c = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/odoo16",
                "sergi",
                "odoo1234"); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "1");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                category.setId(rs.getInt("id"));
                category.setId(rs.getInt("odoo_id"));
                category.setId(rs.getInt("prestashop_id"));
                category.setOdoo_name(rs.getString("odoo_name"));
                category.setOdoo_name(rs.getString("prestashop_name"));
                category.setOdoo_name(rs.getString("id_parent"));
                category.setOdoo_name(rs.getString("active"));
                categories.add(category);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public void insertCategoryOdooPrestashop(
            Integer odoo_id,
            Integer prestashop_id,
            String odoo_name,
            String prestashop_name,
            String id_parent,
            String active
    ) throws SQLException {

        String sql = """
        INSERT INTO category_odoo_prestashop
        (odoo_id, prestashop_id, odoo_name, prestashop_name, id_parent, active)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/odoo16",
                "sergi",
                "odoo1234"); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, odoo_id);
            ps.setInt(2, prestashop_id);
            ps.setString(3, odoo_name);
            ps.setString(4, prestashop_name);
            ps.setString(5, id_parent);
            ps.setString(6, active);

            ps.executeUpdate();
        }
    }

}
