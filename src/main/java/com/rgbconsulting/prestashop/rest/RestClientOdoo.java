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
 * REST client for interacting with Odoo ERP system. Provides methods to manage
 * products, stock, categories, and synchronization with PrestaShop. Handles
 * CRUD operations for product templates, product variants, stock quantities,
 * and categories. Includes database operations for managing Odoo-PrestaShop
 * category mappings.
 *
 * @author sergi
 */
public class RestClientOdoo {

    private static final String URL = "http://localhost:8069/";
    private static String db = "odoo16";
    private static String uid = "2";
    private static String pwd = "admin";
    private OdooConnection oc;

    /**
     * Retrieves all product templates from Odoo. Fetches product information
     * including ID, name, prices, category, and reference code.
     *
     * @return List of ProductTemplate objects containing product information
     * @throws Exception if an error occurs during the search_read operation
     */
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

    /**
     * Retrieves all product variants (product.product) from Odoo. Fetches the
     * product variant ID and its associated template ID.
     *
     * @return List of ProductProduct objects containing product variant
     * information
     * @throws Exception if an error occurs during the search_read operation
     */
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

    /**
     * Creates a new product template in Odoo.
     *
     * @param product ProductTemplate object containing the product data to
     * create
     * @return Integer ID of the newly created product
     * @throws Exception if an error occurs during the create operation
     */
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

    /**
     * Updates an existing product template in Odoo.
     *
     * @param product ProductTemplate object containing the updated product data
     * @return Boolean indicating whether the update was successful
     * @throws Exception if an error occurs during the update operation
     */
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

    /**
     * Deletes a product template from Odoo.
     *
     * @param product ProductTemplate object to delete
     * @return Recordset containing the result of the delete operation
     * @throws Exception if an error occurs during the delete operation
     */
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

    /**
     * Retrieves all stock quantities from Odoo. Fetches stock information from
     * stock.quant model including product ID and quantity.
     *
     * @return List of Stock objects containing stock quantity information
     * @throws Exception if an error occurs during the search_read operation
     */
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

    /**
     * Gets the stock quantity for a specific product. Searches through the
     * provided stock list to find the quantity for the given product ID.
     *
     * @param product_id ID of the product to search for
     * @param stocks List of Stock objects to search through
     * @return Double value representing the product quantity (0.0 if not found)
     */
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

    /**
     * Retrieves all product categories from Odoo. Fetches category information
     * including ID, name, complete name, and parent ID. Handles top-level
     * categories (without parent) by setting parent_id to null.
     *
     * @return List of ProductCategory objects containing category information
     * @throws Exception if an error occurs during the search_read operation
     */
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
                product.setName((String) record.get("name"));

                product.setComplete_Name((String) record.get("complete_name"));
                Object parent = record.get("parent_id");
                if (parent instanceof Object[] parentArr && parentArr.length > 0) {
                    // first element is the parent ID
                    Object idObj = parentArr[0];
                    if (idObj instanceof Integer) {
                        product.setParent_Id((Integer) idObj);
                    } else {
                        product.setParent_Id(null); // unexpected type
                    }
                } else {
                    // top-level category (parent_id == false)
                    product.setParent_Id(null);
                }

                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Downloads an image from a given URL and saves it to a temporary file.
     *
     * @param imageUrl URL string of the image to download
     * @return File object representing the temporary file containing the
     * downloaded image
     * @throws Exception if an error occurs during download or file creation
     */
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

    /**
     * Initializes or returns the existing Odoo connection. Thread-safe
     * singleton pattern for OdooConnection instance.
     *
     * @return OdooConnection instance configured with URL, database, user ID,
     * and password
     * @throws Exception if an error occurs during connection initialization
     */
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

    /**
     * Retrieves all active category mappings between Odoo and PrestaShop from
     * the database. Queries the category_odoo_prestashop table for active
     * records.
     *
     * @return List of CategoryOdooPrestashop objects containing category
     * mapping information
     */
    public List<CategoryOdooPrestashop> getAllCategoriesOdooPrestashop() {
        List<CategoryOdooPrestashop> categories = new ArrayList<>();
        String sql = "SELECT * FROM category_odoo_prestashop WHERE active = ?";

        try (Connection c = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/odoo16",
                "sergi",
                "odoo1234"); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "1");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CategoryOdooPrestashop category = new CategoryOdooPrestashop();

                    category.setId(rs.getInt("id"));
                    category.setOdoo_id(rs.getInt("odoo_id"));
                    category.setPrestashop_id(rs.getInt("prestashop_id"));
                    category.setOdoo_name(rs.getString("odoo_name"));
                    category.setPrestashop_name(rs.getString("prestashop_name"));
                    category.setId_parent(rs.getString("id_parent"));
                    category.setActive(rs.getString("active"));

                    categories.add(category);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Inserts a new category mapping between Odoo and PrestaShop into the
     * database. Checks if the category already exists before inserting to avoid
     * duplicates.
     *
     * @param odoo_id ID of the category in Odoo
     * @param prestashop_id ID of the category in PrestaShop (can be null)
     * @param odoo_name Name of the category in Odoo
     * @param prestashop_name Name of the category in PrestaShop
     * @param id_parent ID of the parent category
     * @param active Active status flag ("0" or "1")
     * @throws SQLException if a database error occurs during insertion
     */
    public void insertCategoryOdooPrestashop(
            Integer odoo_id,
            Integer prestashop_id,
            String odoo_name,
            String prestashop_name,
            String id_parent,
            String active
    ) throws SQLException {

        String checkSql = "SELECT 1 FROM category_odoo_prestashop WHERE odoo_id = ?";
        String insertSql = """
        INSERT INTO category_odoo_prestashop
        (odoo_id, prestashop_id, odoo_name, prestashop_name, id_parent, active)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        try (Connection c = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/odoo16",
                "sergi",
                "odoo1234"); PreparedStatement checkPs = c.prepareStatement(checkSql); PreparedStatement insertPs = c.prepareStatement(insertSql)) {

            // Check if the category already exists
            checkPs.setInt(1, odoo_id);
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) { // does not exist → insert
                insertPs.setInt(1, odoo_id);
                if (prestashop_id != null) {
                    insertPs.setInt(2, prestashop_id);
                } else {
                    insertPs.setNull(2, java.sql.Types.BIGINT);
                }
                insertPs.setString(3, odoo_name);
                insertPs.setString(4, prestashop_name);
                insertPs.setString(5, id_parent);
                insertPs.setString(6, active);

                insertPs.executeUpdate();
            }
        }
    }

    /**
     * Updates the PrestaShop ID for an existing Odoo category mapping in the
     * database.
     *
     * @param odoo_id ID of the category in Odoo
     * @param prestashop_id New PrestaShop ID to update (can be null)
     * @throws SQLException if a database error occurs during the update
     */
    public void updatePrestashopId(
            Integer odoo_id,
            Integer prestashop_id
    ) throws SQLException {

        String updateSql = """
        UPDATE category_odoo_prestashop
        SET prestashop_id = ?
        WHERE odoo_id = ?
    """;

        try (Connection c = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/odoo16",
                "sergi",
                "odoo1234"); PreparedStatement ps = c.prepareStatement(updateSql)) {

            if (prestashop_id != null) {
                ps.setInt(1, prestashop_id);
            } else {
                ps.setNull(1, java.sql.Types.BIGINT);
            }

            ps.setInt(2, odoo_id);

            ps.executeUpdate();
        }
    }

    /**
     * Checks if a category mapping exists in the database for a given Odoo ID.
     *
     * @param odooId Odoo category ID to check
     * @return Boolean indicating whether the category mapping exists
     * @throws SQLException if a database error occurs during the check
     */
    public boolean existsCategoryByOdooId(Integer odooId) throws SQLException {

        String sql = "SELECT 1 FROM category_odoo_prestashop WHERE odoo_id = ?";

        try (Connection c = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/odoo16",
                "sergi",
                "odoo1234"); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, odooId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

}
