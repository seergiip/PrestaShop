package com.rgbconsulting.prestashop.model;

import com.rgbconsulting.prestashop.common.odoo.model.Product;
import java.io.StringReader;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
/**
 *
 * @author sergi
 */
public class PrestaShopParser {

    public static List<Product> parseProducts(String xml) throws Exception {
        List<Product> products = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));

        NodeList productNodes = doc.getElementsByTagName("product");
        for (int i = 0; i < productNodes.getLength(); i++) {
            Element productElem = (Element) productNodes.item(i);

            String psIdStr = getTextContent(productElem, "id");
            Integer prestashopId = psIdStr.isEmpty() ? null : Integer.parseInt(psIdStr);

            String reference = getTextContent(productElem, "reference");

            // Para el nombre
            String name = "";
            NodeList nameNodes = productElem.getElementsByTagName("name");
            if (nameNodes.getLength() > 0) {
                Element nameElem = (Element) nameNodes.item(0);
                NodeList langNodes = nameElem.getElementsByTagName("language");
                if (langNodes.getLength() > 0 && langNodes.item(0) != null) {
                    name = langNodes.item(0).getTextContent();
                } else {
                    name = nameElem.getTextContent();
                }
            }

            double sales_price = Double.parseDouble(getTextContent(productElem, "price").isEmpty() ? "0" : getTextContent(productElem, "price"));
            double cost = Double.parseDouble(getTextContent(productElem, "wholesale_price").isEmpty() ? "0" : getTextContent(productElem, "wholesale_price"));

            Product product = new Product();
            product.setPrestashopId(prestashopId);
            product.setReference(reference);
            product.setName(name);
            product.setSales_price(sales_price);
            product.setCost(cost);

            products.add(product);
        }

        return products;
    }

    // --- Método utilitario para evitar NullPointerException ---
    private static String getTextContent(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0 && nodes.item(0) != null) {
            return nodes.item(0).getTextContent();
        }
        return ""; // valor por defecto si no existe
    }
}
