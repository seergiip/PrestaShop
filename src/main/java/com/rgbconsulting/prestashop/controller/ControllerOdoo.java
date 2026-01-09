package com.rgbconsulting.prestashop.controller;

import com.rgbconsulting.prestashop.rest.RestClientOdoo;

/**
 *
 * @author sergi
 */
public class ControllerOdoo {
    RestClientOdoo restClientOdoo = new RestClientOdoo();
    
    public void getProduct() {
        restClientOdoo.get();
    }
}
