/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rgbconsulting.prestashop.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

/**
 *
 * @author sergi
 */
public class RestClientOdoo {

    private static final String URL = "http://localhost:8080/training-app/api/jta/client/";
    private HttpClient client = initClient(this.client);

    /*
        Iniciar el client
     */
    private HttpClient initClient(HttpClient client) {
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
    private void get(HttpClient client) {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(URL))
                    .header("Authorization", "1234")
                    .GET()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }
    }
}
