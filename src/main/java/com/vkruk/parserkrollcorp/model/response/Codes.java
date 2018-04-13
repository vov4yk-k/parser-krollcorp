package com.vkruk.parserkrollcorp.model.response;

import com.vkruk.parserkrollcorp.entity.Product;

public class Codes {

    private String upc;
    private String seller_sku;
    private String mfg_sku;

    public Codes() {
    }

    public Codes(Product product) {
        this.upc = product.getUpc();
        this.seller_sku = product.getSku();
        this.mfg_sku = product.getManufacturerPartNumber();
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getSeller_sku() {
        return seller_sku;
    }

    public void setSeller_sku(String seller_sku) {
        this.seller_sku = seller_sku;
    }

    public String getMfg_sku() {
        return mfg_sku;
    }

    public void setMfg_sku(String mfg_sku) {
        this.mfg_sku = mfg_sku;
    }
}
