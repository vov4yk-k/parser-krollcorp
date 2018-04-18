package com.vkruk.parserkrollcorp.model.response;

import com.vkruk.parserkrollcorp.entity.Product;

import java.util.HashMap;


public class ParentProductResponse extends ProductResponse {

    private ProductResponse[] variants;

    public ParentProductResponse(Product parentProduct, ProductResponse[] variants) {
        super(parentProduct);
        if(variants.length > 0){
            this.setPrice(0);
            this.setQty(0);
            this.setMsrp(0);
            this.setMapPrice(0);
            this.setVprop(null);
            this.setImages(null);
            this.setCodes(null);
        }
        this.variants = variants;
    }

    public ProductResponse[] getVariants() {
        return variants;
    }

    public void setVariants(ProductResponse[] variants) {
        this.variants = variants;
    }
}
