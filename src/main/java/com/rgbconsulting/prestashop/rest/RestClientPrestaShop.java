package com.rgbconsulting.prestashop.rest;

import com.rgbconsulting.prestashop.common.odoo.model.Product;
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
    public List<Product> getAllProducts(HttpClient client) {
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
    public void uploadProduct(HttpClient client, String xmlProduct) {
        HttpRequest request = null;
        HttpResponse response;
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
                System.out.println("POST Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    // PUT
    /*
        Updates a product from the prestashop
        If the product doesnt't exists, it shouts an error.
        If the product exists, it updates it.
     */
    public void updateProduct(HttpClient client, String xmlProduct) {
        HttpRequest request = null;
        HttpResponse response;
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
                System.out.println("PUT Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
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
    public void uploadCategory(HttpClient client, String xmlCategory) {
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(URL + "/products/"))
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
    }

}
