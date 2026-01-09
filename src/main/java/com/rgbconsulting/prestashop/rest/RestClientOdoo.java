package com.rgbconsulting.prestashop.rest;

import com.rgbconsulting.prestashop.common.odoo.model.connection.OdooConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 *
 * @author sergi
 */
public class RestClientOdoo {

    private static final String URL = "http://localhost:8069/api/jta/client/";
    private HttpClient client = initClient();
    private static String url = "";
    private static String db = "";
    private static String uid = "";
    private static String pwd = "";
    OdooConnection oc;

    /*
        Iniciar el client
     */
    private HttpClient initClient() {
        return client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    // GET
    /*
        Get the product from odoo
     */
    public void get() {
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(URL))
                    .header("Authorization", "1234")
                    .GET()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }
        
        if (request != null) {
            try {
                response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("GET Status Code: " + response.statusCode());
                System.out.println("GET Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }
    
    private void startConnection () {
        try {
            oc = new OdooConnection(url, db, uid, pwd);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
