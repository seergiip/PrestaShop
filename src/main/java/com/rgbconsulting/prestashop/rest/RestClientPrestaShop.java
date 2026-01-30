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
 *
 * @author sergi
 *
 *
 */
public class RestClientPrestaShop {

    private String URL = "";
    private String KEY = "";
    private String auth = "";
    private String encodedAuth = "";
    private String secret_key = "dXJ8pOkykWANFh8M";

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


    /*
        Iniciar el client
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

    // GET
    /*
        Get the product from the prestashop using the id sent on the parameter
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

    // GET
    /*
        Get all the products from the prestashop
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

    // POST
    /*
        Uploads a product to the prestashop
        If the product already exists it adds to its cuantity.
        If the product doesn't exist it adds like a new product.
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

    // PUT
    /*
        Updates a product from the prestashop
        If the product doesnt't exists, it shouts an error.
        If the product exists, it updates it.
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

    // DELETE
    /*
        Deletes a product from the prestashop using the id
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

    // POST
    /*
        Uploads a product to the prestashop
        If the product already exists it adds to its cuantity.
        If the product doesn't exist it adds like a new product.
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
                System.out.println("POST Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
        return response;
    }

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

                String id_parent = categoryElement
                        .getElementsByTagName("id_parent")
                        .item(0)
                        .getTextContent()
                        .trim();

                String name = categoryElement
                        .getElementsByTagName("name")
                        .item(0)
                        .getTextContent()
                        .trim();

                String active = categoryElement
                        .getElementsByTagName("active")
                        .item(0)
                        .getTextContent()
                        .trim();

                CategoryMapper categoryMapper
                        = new CategoryMapper(name, id_parent, active);

                categories.add(categoryMapper);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }

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
