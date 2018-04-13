package com.vkruk.parserkrollcorp.model.response;

import com.vkruk.parserkrollcorp.entity.Product;

import java.util.HashMap;

public class ProductResponse {

    private  String id;
    private  Codes codes;
    private String name;
    private String brand;
    private String sdesc;
    private String desc;
    private int qty;
    private double price;
    private String msrp;
    private HashMap<String,String> vprop;
    private HashMap<String,String> prop;
    private String[] images;

    public ProductResponse(){}

    public ProductResponse(Product product){
        this.id = product.getProductbvin();
        this.codes = new Codes(product);
        this.name = product.getName();
        this.brand = product.getBrand();
        this.sdesc = product.getShortDescription();
        this.desc = product.getDescription();
        setProps(product);
        setVProps(product);
        this.images = new String[]{product.getImageUrl()};
        this.price = product.getNumPrice();
        this.msrp = product.getListPrice();
        this.qty = product.getQty();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Codes getCodes() {
        return codes;
    }

    public void setCodes(Codes codes) {
        this.codes = codes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSdesc() {
        return sdesc;
    }

    public void setSdesc(String sdesc) {
        this.sdesc = sdesc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public HashMap<String,String> getProp() {
        return prop;
    }

    public void setProp(HashMap<String,String> prop) {
        this.prop = prop;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public HashMap<String, String> getVprop() {
        return vprop;
    }

    public void setVprop(HashMap<String, String> vprop) {
        this.vprop = vprop;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getMsrp() {
        return msrp;
    }

    public void setMsrp(String msrp) {
        this.msrp = msrp;
    }

    void setProps(Product product){

        this.prop = new HashMap<>();
        this.prop.put("isHazardous",product.isHazardous()?"yes":"no");
        this.prop.put("countryofOrigin", product.getCountryofOrigin());
        this.prop.put("productWeight", product.getProductWeight());
        this.prop.put("mapPrice", product.getMAPPrice());

    }

    void setVProps(Product product){

        this.vprop = new HashMap<>();

        product.getProperties().forEach( productProperty -> {
            this.vprop.put(productProperty.getName(), productProperty.getValueName());
        });

    }
}
