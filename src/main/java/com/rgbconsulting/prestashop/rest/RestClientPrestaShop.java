package com.rgbconsulting.prestashop.rest;

import com.rgbconsulting.prestashop.common.odoo.model.ProductTemplate;
import com.rgbconsulting.prestashop.model.PrestaShopParser;
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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

/**
 *
 * @author sergi
 *
 * What does it do? -> Connexion to PrestaShop (From here you can GET, PUT,
 * DELETE, UPDATE all the products of the shop.
 */
public class RestClientPrestaShop {

    private static final String URL = "https://prestashopsergi.com/api";
    private static final String KEY = "U69EHDTLP19486EPGYJUFEFPXPQX7Q11";
    private static final String auth = KEY + ":";
    private static final String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

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
                    .uri(new URI(URL + "/products/?display=full"))
                    .header("Authorization", "Basic " + encodedAuth)
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
                    .uri(new URI(URL + "/products/"))
                    .header("Authorization", "Basic " + encodedAuth)
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
                    .uri(new URI(URL + "/products/"))
                    .header("Authorization", "Basic " + encodedAuth)
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
        Deletes a product from the prestashop
     */
    public void deleteProductById(HttpClient client, String id) {
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(URL + "/products/" + id))
                    .header("Authorization", "Basic " + encodedAuth)
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
                    .uri(new URI(URL + "/categories"))
                    .header("Authorization", "Basic " + encodedAuth)
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

            // Tomamos el primer <category> de la respuesta
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
                return id; // Retornamos solo el ID
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
                    .uri(new URI(URL + "/stock_availables?filter[id_product]=" + id_product + "&display=full"))
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

            // Tomamos el primer <category> de la respuesta
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
                    .uri(new URI(URL + "/stock_availables/"))
                    .header("Authorization", "Basic " + encodedAuth)
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
                    .uri(new URI(URL + "/stock_availables/"))
                    .header("Authorization", "Basic " + encodedAuth)
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

            // Tomamos el primer <category> de la respuesta
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
                    .uri(new URI(URL + "/products?filter[reference]=" + reference + "&display=full"))
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
                //System.out.println("GET Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
        return response;
    }
}
