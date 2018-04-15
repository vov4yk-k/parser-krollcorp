package com.vkruk.parserkrollcorp.controller;

import com.vkruk.parserkrollcorp.model.response.ParentProductResponse;
import com.vkruk.parserkrollcorp.services.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Api(value="Products", description="Parsed product data")
@RequestMapping("/api")
@BasePathAwareController
public class ProductController {

    private ProductService productService;


    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @ApiOperation(value = "All products")
    @RequestMapping(value = "/products", method = RequestMethod.GET , produces = RestMediaTypes.SCHEMA_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<ParentProductResponse>> getProducts(){
        return ResponseEntity.ok(productService.getProducts());
    }

    @ApiOperation(value = "Products by parsing session ID")
    @RequestMapping(value = "/products_by_id", method = RequestMethod.GET , produces = RestMediaTypes.SCHEMA_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<ParentProductResponse>> getProducts(long parseId){
        return ResponseEntity.ok(productService.getProductsByParseId(parseId));
    }
}
