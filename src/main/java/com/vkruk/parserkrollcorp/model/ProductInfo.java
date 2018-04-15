package com.vkruk.parserkrollcorp.model;

import com.vkruk.parserkrollcorp.entity.ProductProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductInfo {

    @JsonProperty
    private String Message;

    @JsonProperty
    private String ImageUrl;

    @JsonProperty
    private String Price;

    @JsonProperty
    private String Sku;

    @JsonProperty
    private String StockMessage;

    @JsonProperty
    private boolean IsValid;

    @JsonProperty
    private String UPCCode;

    @JsonProperty
    private String ManufacturerPartNumber;

    @JsonProperty
    private String ShortDescription;

    @JsonProperty
    private String Weight;

    @JsonProperty
    private String ListPrice;

    @JsonProperty
    private String MAPPrice;

    @JsonProperty
    private String CountryOfOrigin;

    Set<ProductProperty> properties;

    public ProductInfo() {
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getSku() {
        return Sku;
    }

    public void setSku(String sku) {
        Sku = sku;
    }

    public String getStockMessage() {
        return StockMessage;
    }

    public void setStockMessage(String stockMessage) {
        StockMessage = stockMessage;
    }

    public boolean isValid() {
        return IsValid;
    }

    public void setValid(boolean valid) {
        IsValid = valid;
    }

    public String getUPCCode() {
        return UPCCode;
    }

    public void setUPCCode(String UPCCode) {
        this.UPCCode = UPCCode;
    }

    public String getManufacturerPartNumber() {
        return ManufacturerPartNumber;
    }

    public void setManufacturerPartNumber(String manufacturerPartNumber) {
        ManufacturerPartNumber = manufacturerPartNumber;
    }

    public String getShortDescription() {
        return ShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        ShortDescription = shortDescription;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getListPrice() {
        return ListPrice;
    }

    public void setListPrice(String listPrice) {
        ListPrice = listPrice;
    }


    public Set<ProductProperty> getProperties() {
        return properties;
    }

    public void setProperties(Set<ProductProperty> properties) {
        this.properties = properties;
    }

    public String getMAPPrice() {
        return MAPPrice;
    }

    public void setMAPPrice(String MAPPrice) {
        this.MAPPrice = MAPPrice;
    }

    public String getCountryOfOrigin() {
        return CountryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        CountryOfOrigin = countryOfOrigin;
    }

    public double getNumPrice() {
        double price = 0.0;
        Document doc = Jsoup.parse(this.Price);
        try {
            price = Double.parseDouble(doc.select("span").last().text().replace("$",""));
        }catch (NumberFormatException e){
        }
        return price;
    }

    public long getUPCCodeLong() {
        long code = 0;
        try {
            code = Long.parseLong(UPCCode);
        }catch (ArithmeticException e){ }
        return code;
    }
}
