package com.rgbconsulting.prestashop.rest;

import com.rgbconsulting.prestashop.common.odoo.model.ProductTemplate;
import com.rgbconsulting.prestashop.mapper.CategoryMapper;
import com.rgbconsulting.prestashop.model.PrestaShopParser;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

/**
 * REST client for interacting with PrestaShop e-commerce platform API. Provides
 * methods to manage products, categories, stock, and images. Handles CRUD
 * operations with XML-based requests and responses. Includes configuration
 * loading with AES encryption/decryption and SSL certificate handling.
 *
 * @author sergi
 */
public class RestClientPrestaShop {

    private String URL = "";
    private String KEY = "";
    private String auth = "";
    private String encodedAuth = "";
    private String secret_key = "dXJ8pOkykWANFh8M";

    /**
     * Loads PrestaShop connection properties from configuration file. Decrypts
     * URL and API key using AES encryption. Configures Basic authentication
     * credentials.
     *
     * @throws FileNotFoundException if the config.properties file is not found
     * @throws IOException if an error occurs reading the properties file
     * @throws IllegalStateException if URL or KEY properties are missing
     */
    public void loadProperties() throws FileNotFoundException, IOException {
        Properties p = new Properties();
        p.load(new BufferedReader(new FileReader("files/properties/config.properties")));
        String url = p.getProperty("url");
        this.URL = decrypt(url, secret_key);
        String key = p.getProperty("key");
        this.KEY = decrypt(key, secret_key);
        this.auth = KEY + ":";
        this.encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        if (URL == null || KEY == null) {
            throw new IllegalStateException("Faltan variables de entorno de PrestaShop");
        }
    }

    /**
     * Decrypts an AES-encrypted string using ECB mode without padding. Removes
     * manual padding from the decrypted bytes.
     *
     * @param encryptedData Base64-encoded encrypted string
     * @param secret AES secret key (must be 16 bytes)
     * @return Decrypted string, or null if decryption fails
     */
    public String decrypt(String encryptedData, String secret) {
        try {
            // Preparar clave y cifrador
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding"); // Sin padding automático
            cipher.init(Cipher.DECRYPT_MODE, key);

            // Decodificar Base64
            byte[] decodedVal = Base64.getDecoder().decode(encryptedData);

            // Desencriptar
            byte[] decryptedBytes = cipher.doFinal(decodedVal);

            // Quitar padding manual (último byte indica cantidad de padding)
            int pad = decryptedBytes[decryptedBytes.length - 1];
            int length = decryptedBytes.length - pad;
            return new String(decryptedBytes, 0, length, StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Initializes an HTTP client with SSL certificate verification disabled.
     * Configures TLS context to trust all certificates. Sets HTTP/1.1, normal
     * redirects, and 20-second connection timeout.
     *
     * @return HttpClient configured with custom SSL context
     * @throws RuntimeException if an error occurs during client initialization
     */
    public HttpClient initClient() {

        try {
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");

            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    .sslContext(sslContext) // Ignora la cadena de confianza
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar HttpClient", e);
        }
    }

    /**
     * Retrieves a specific product from PrestaShop by its ID. Prints the status
     * code and response body to console.
     *
     * @param client HttpClient instance to use for the request
     * @param idProduct ID of the product to retrieve
     */
    public void getProductbyId(HttpClient client, String idProduct) {
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(URL + "/products/" + idProduct))
                    .header("Authorization", "Basic " + encodedAuth)
                    .GET()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("GET Status Code: " + response.statusCode());
                System.out.println("GET Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    /**
     * Retrieves all products from PrestaShop with full display details. Parses
     * the XML response into a list of ProductTemplate objects.
     *
     * @param client HttpClient instance to use for the request
     * @return List of ProductTemplate objects, or empty list if an error occurs
     */
    public List<ProductTemplate> getAllProducts(HttpClient client) {
        HttpRequest request = null;
        HttpResponse<String> response = null;

        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "/products/?display=full"))
                    .header("Authorization", "Basic " + this.encodedAuth)
                    .GET()
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return List.of();
        }

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("GET Status Code: " + response.statusCode());
            // Parseamos el XML directamente a lista de Product
            return PrestaShopParser.parseProducts(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Uploads a new product to PrestaShop. If the product already exists, it
     * adds to its quantity. If the product doesn't exist, it adds it as a new
     * product.
     *
     * @param client HttpClient instance to use for the request
     * @param xmlProduct XML string containing the product data
     * @return HttpResponse object containing the server response
     */
    public HttpResponse uploadProduct(HttpClient client, String xmlProduct) {
        HttpRequest request = null;
        HttpResponse response = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "/products/"))
                    .header("Authorization", "Basic " + this.encodedAuth)
                    .POST(BodyPublishers.ofString(xmlProduct))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("POST Status Code: " + response.statusCode());
                //System.out.println("POST Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
        return response;
    }

    /**
     * Updates an existing product in PrestaShop. If the product doesn't exist,
     * it returns an error. If the product exists, it updates it.
     *
     * @param client HttpClient instance to use for the request
     * @param xmlProduct XML string containing the updated product data
     * @return HttpResponse object containing the server response
     */
    public HttpResponse updateProduct(HttpClient client, String xmlProduct) {
        HttpRequest request = null;
        HttpResponse response = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "/products/"))
                    .header("Authorization", "Basic " + this.encodedAuth)
                    .PUT(BodyPublishers.ofString(xmlProduct))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("PUT Status Code: " + response.statusCode());
                //System.out.println("PUT Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
        return null;
    }

    /**
     * Deletes a product from PrestaShop by its ID. Prints the status code and
     * response body to console.
     *
     * @param client HttpClient instance to use for the request
     * @param id ID of the product to delete
     */
    public void deleteProductById(HttpClient client, String id) {
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "/products/" + id))
                    .header("Authorization", "Basic " + this.encodedAuth)
                    .DELETE()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("DELETE Status Code: " + response.statusCode());
                System.out.println("DELETE Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    /**
     * Uploads a new category to PrestaShop. If the category already exists, it
     * adds to its quantity. If the category doesn't exist, it adds it as a new
     * category.
     *
     * @param client HttpClient instance to use for the request
     * @param xmlCategory XML string containing the category data
     * @return HttpResponse object containing the server response
     */
    public HttpResponse uploadCategory(HttpClient client, String xmlCategory) {
        HttpRequest request = null;
        HttpResponse response = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "/categories"))
                    .header("Authorization", "Basic " + this.encodedAuth)
                    .POST(BodyPublishers.ofString(xmlCategory))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("POST Status Code: " + response.statusCode());
                //System.out.println("POST Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
        return response;
    }

    /**
     * Extracts the category ID from the HTTP response. Parses the XML response
     * body to retrieve the category ID and name. Prints the created category
     * information to console.
     *
     * @param response HttpResponse containing the category creation result
     * @return String ID of the created category, or null if parsing fails
     */
    public String getCategoryId(HttpResponse<String> response) {
        try {
            String xml = response.body();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            Element category = (Element) doc.getElementsByTagName("category").item(0);

            if (category != null) {
                String id = category.getElementsByTagName("id").item(0).getTextContent().trim();
                String name = "";

                NodeList nameNodes = category.getElementsByTagName("name");
                if (nameNodes.getLength() > 0) {
                    NodeList langNodes = ((Element) nameNodes.item(0)).getElementsByTagName("language");
                    for (int j = 0; j < langNodes.getLength(); j++) {
                        Element lang = (Element) langNodes.item(j);
                        if ("1".equals(lang.getAttribute("id"))) {
                            name = lang.getTextContent().trim();
                            break;
                        }
                    }
                }

                System.out.println("Created category: ID=" + id + " | Name=" + name);
                return id;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the available stock information for a specific product. Queries
     * the stock_availables endpoint with product ID filter.
     *
     * @param client HttpClient instance to use for the request
     * @param id_product ID of the product to query stock for
     * @return HttpResponse containing the stock availability data
     */
    public HttpResponse getAvailableStock(HttpClient client, String id_product) {
        HttpRequest request = null;
        HttpResponse response = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "/stock_availables?filter[id_product]=" + id_product + "&display=full"))
                    .header("Authorization", "Basic " + this.encodedAuth)
                    .GET()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("GET Status Code: " + response.statusCode());
                //System.out.println("GET Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
        return response;
    }

    /**
     * Extracts the stock ID from the stock availability HTTP response. Parses
     * the XML response body to retrieve the stock_available ID.
     *
     * @param response HttpResponse containing the stock availability data
     * @return String ID of the stock record, or null if parsing fails
     */
    public String getIdOfStock(HttpResponse response) {
        try {
            String xml = (String) response.body();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            Element category = (Element) doc.getElementsByTagName("stock_available").item(0);

            if (category != null) {
                String id = category.getElementsByTagName("id").item(0).getTextContent().trim();

                return id;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Uploads stock information to PrestaShop using PATCH method. Updates the
     * stock_availables endpoint with new stock data. Prints the status code to
     * console.
     *
     * @param client HttpClient instance to use for the request
     * @param xmlStock XML string containing the stock data
     */
    public void uploadStock(HttpClient client, String xmlStock) {

        HttpRequest request;

        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "/stock_availables/"))
                    .header("Authorization", "Basic " + this.encodedAuth)
                    .header("Content-Type", "application/xml")
                    .method("PATCH", BodyPublishers.ofString(xmlStock))
                    .build();

            HttpResponse<String> response
                    = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("PATCH Status Code: " + response.statusCode());
            //System.out.println("PATCH Response Body: " + response.body());

        } catch (URISyntaxException e) {
            System.err.println("URI inválida");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates existing stock information in PrestaShop using PUT method.
     * Modifies the stock_availables endpoint with updated stock data.
     *
     * @param client HttpClient instance to use for the request
     * @param xmlStock XML string containing the updated stock data
     * @param stock_id ID of the stock record to update
     */
    public void updateStock(HttpClient client, String xmlStock, String stock_id) {
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "/stock_availables/"))
                    .header("Authorization", "Basic " + this.encodedAuth)
                    .PUT(BodyPublishers.ofString(xmlStock))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("PUT Status Code: " + response.statusCode());
                //System.out.println("PUT Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    /**
     * Extracts the product ID from an HTTP response. Parses the XML response
     * body to retrieve the product ID.
     *
     * @param response HttpResponse containing product data
     * @return String ID of the product, or null if parsing fails
     */
    public String getProductIdFromResponse(HttpResponse<String> response) {
        try {
            String xml = response.body();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            Element product = (Element) doc.getElementsByTagName("product").item(0);

            if (product != null) {
                String id = product.getElementsByTagName("id").item(0).getTextContent().trim();

                return id;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a product from PrestaShop by its reference code. Queries the
     * products endpoint with reference filter and full display.
     *
     * @param client HttpClient instance to use for the request
     * @param reference Reference code of the product to retrieve
     * @return HttpResponse containing the product data
     */
    public HttpResponse getProductByReference(HttpClient client, String reference) {
        HttpRequest request = null;
        HttpResponse response = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "/products?filter[reference]=" + reference + "&display=full"))
                    .header("Authorization", "Basic " + this.encodedAuth)
                    .GET()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("GET Status Code: " + response.statusCode());
                //System.out.println("GET Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
        return response;
    }

    /**
     * Retrieves all categories from PrestaShop with full display details.
     * Queries the categories endpoint.
     *
     * @param client HttpClient instance to use for the request
     * @return HttpResponse containing all categories data
     */
    public HttpResponse getPrestaShopCategories(HttpClient client) {
        HttpRequest request = null;
        HttpResponse response = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "/categories?display=full"))
                    .header("Authorization", "Basic " + this.encodedAuth)
                    .GET()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("GET Status Code: " + response.statusCode());
                //System.out.println("GET Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
        return response;
    }

    /**
     * Parses PrestaShop categories from HTTP response into a list of
     * CategoryMapper objects. Extracts category name, parent ID, and active
     * status from XML response. Handles multilingual category names by taking
     * the first language.
     *
     * @param response HttpResponse containing categories XML data
     * @return List of CategoryMapper objects with parsed category information
     */
    public List<CategoryMapper> getPrestaShopCategoriesToList(HttpResponse response) {
        List<CategoryMapper> categories = new ArrayList<>();

        try {
            String xml = (String) response.body();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            NodeList categoryNodes = doc.getElementsByTagName("category");

            for (int i = 0; i < categoryNodes.getLength(); i++) {
                Element categoryElement = (Element) categoryNodes.item(i);

                String id_parent = "";
                NodeList parentNodes = categoryElement.getElementsByTagName("id_parent");
                if (parentNodes != null && parentNodes.getLength() > 0 && parentNodes.item(0) != null) {
                    id_parent = parentNodes.item(0).getTextContent().trim();
                }

                // active
                String active = "";
                NodeList activeNodes = categoryElement.getElementsByTagName("active");
                if (activeNodes != null && activeNodes.getLength() > 0 && activeNodes.item(0) != null) {
                    active = activeNodes.item(0).getTextContent().trim();
                }

                // name (handle multilingual <language> children)
                String name = "";
                NodeList nameNodes = categoryElement.getElementsByTagName("name");
                if (nameNodes != null && nameNodes.getLength() > 0 && nameNodes.item(0) != null) {
                    Node nameNode = nameNodes.item(0);
                    NodeList languages = nameNode.getChildNodes();
                    for (int j = 0; j < languages.getLength(); j++) {
                        Node lang = languages.item(j);
                        if ("language".equals(lang.getNodeName())) {
                            name = lang.getTextContent().trim();
                            break; // take only the first language
                        }
                    }
                }

                CategoryMapper categoryMapper
                        = new CategoryMapper(name, id_parent, active);

                categories.add(categoryMapper);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Uploads a product image to PrestaShop. First retrieves the product by
     * reference to get its PrestaShop ID. Constructs a multipart/form-data
     * request with the image file. Uses the images/products endpoint for
     * upload.
     *
     * @param client HttpClient instance to use for the request
     * @param imageFile File object containing the image to upload
     * @param productReference Reference code of the product to attach the image
     * to
     * @throws Exception if product is not found, or upload fails
     * @throws RuntimeException if HTTP status is not 200/201
     */
    public void uploadImage(
            HttpClient client,
            File imageFile,
            String productReference
    ) throws Exception {

        // Primero obtenemos el producto de PrestaShop usando la referencia
        HttpResponse<String> getResponse = getProductByReference(client, productReference);

        String statusBody = getResponse.body();
        int statusCode = getResponse.statusCode();

        if (statusCode != 200) {
            throw new RuntimeException(
                    "No se pudo obtener el producto en PrestaShop. HTTP " + statusCode + " -> " + statusBody
            );
        }

        // Parseamos la respuesta para sacar el ID real de PrestaShop
        String prestashopProductId = getProductIdFromResponse(getResponse);

        if (prestashopProductId == null || prestashopProductId.isEmpty()) {
            throw new RuntimeException(
                    "No se encontró el producto en PrestaShop con la referencia: " + productReference
            );
        }

        String boundary = "----Boundary" + System.currentTimeMillis();
        String CRLF = "\r\n";

        // Construimos el body multipart
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Header multipart
        baos.write(("--" + boundary + CRLF).getBytes(StandardCharsets.UTF_8));
        baos.write(("Content-Disposition: form-data; name=\"image\"; filename=\""
                + imageFile.getName() + "\"" + CRLF).getBytes(StandardCharsets.UTF_8));
        baos.write(("Content-Type: image/jpeg" + CRLF).getBytes(StandardCharsets.UTF_8));
        baos.write(CRLF.getBytes(StandardCharsets.UTF_8));

        // Binario de la imagen
        Files.copy(imageFile.toPath(), baos);

        // Cierre multipart
        baos.write(CRLF.getBytes(StandardCharsets.UTF_8));
        baos.write(("--" + boundary + "--" + CRLF).getBytes(StandardCharsets.UTF_8));

        byte[] multipartBytes = baos.toByteArray();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.URL + "/images/products/" + prestashopProductId))
                .header("Authorization", "Basic " + this.encodedAuth)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBytes))
                .build();

        HttpResponse<String> response
                = client.send(request, HttpResponse.BodyHandlers.ofString());

        int uploadStatus = response.statusCode();
        if (uploadStatus != 200 && uploadStatus != 201) {
            throw new RuntimeException(
                    "Error subiendo imagen. HTTP " + uploadStatus + " -> " + response.body()
            );
        }
    }
}
