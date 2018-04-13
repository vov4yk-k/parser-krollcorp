package com.vkruk.parserkrollcorp.controller;

import com.vkruk.parserkrollcorp.model.response.ParentProductResponse;
import com.vkruk.parserkrollcorp.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;




@RequestMapping("/api")
@BasePathAwareController
public class ProductController {

    private ProductService productService;


    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(value = "/prods", method = RequestMethod.GET , produces = RestMediaTypes.SCHEMA_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<ParentProductResponse>> getProducts(){
        List<ParentProductResponse> products = productService.getProducts();
        return ResponseEntity.ok(productService.getProducts());
    }
}
