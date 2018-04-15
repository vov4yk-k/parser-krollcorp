package com.vkruk.parserkrollcorp.entity;

import com.vkruk.parserkrollcorp.model.ProductInfo;
import lombok.Data;

import javax.persistence.*;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;


@Data
@Entity(name = "Product")
@Table(name = "product")
public class Product {

    private @Id  long id;        //#upcCode  @GeneratedValue
    private String upc;
    private String productbvin;                 //#productbvin
    private String name;                        //itemprop="name"
    private String manufacturerPartNumber;      //#isHazardous
    private boolean isHazardous;                //#manufacturerPartNumber
    private String countryofOrigin;             //#countryofOrigin
    private String productWeight;               //#productWeight
    private @Lob String description;                 //.productdescription //itemprop="description"
    private String imageUrl;                    //#imgMain
    private boolean isValid;
    private String price;                       //#msrp
    private String sku;
    private String listPrice;
    private @Lob String shortDescription;
    private String stockMessage;
    private String brand;
    private String MAPPrice;
    private double numPrice;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductProperty> properties = new HashSet<ProductProperty>();


    public Product(){}

    public Product(long id, String productbvin, String name, String manufacturerPartNumber,
                   boolean isHazardous, String countryofOrigin, String productWeight,
                   String description, String imageUrl, boolean isValid, String price,
                   String sku, String listPrice, String shortDescription, String stockMessage,
                   String brand, String upc) {

        this.id = id;
        this.productbvin = productbvin;
        this.name = name;
        this.manufacturerPartNumber = manufacturerPartNumber;
        this.isHazardous = isHazardous;
        this.countryofOrigin = countryofOrigin;
        this.productWeight = productWeight;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isValid = isValid;
        this.price = price;
        this.sku = sku;
        this.listPrice = listPrice;
        this.shortDescription = shortDescription;
        this.stockMessage = stockMessage;
        this.brand = brand;
        this.upc = upc;

    }

    public Product(ProductInfo productInfo){

        this.id = productInfo.getUPCCodeLong();
        this.imageUrl = productInfo.getImageUrl();
        this.price = productInfo.getPrice();
        this.sku = productInfo.getSku();
        this.isValid = productInfo.isValid();
        this.manufacturerPartNumber = productInfo.getManufacturerPartNumber();
        this.shortDescription = productInfo.getShortDescription();
        this.productWeight = productInfo.getWeight();
        this.listPrice = productInfo.getListPrice();
        this.upc = productInfo.getUPCCode();
        this.properties = new HashSet<>();
        this.countryofOrigin = productInfo.getCountryOfOrigin();
        this.MAPPrice = productInfo.getMAPPrice();
        this.numPrice = productInfo.getNumPrice();
        this.stockMessage = productInfo.getStockMessage();

        productInfo.getProperties().forEach(productProperty -> {
            this.addProperty(new ProductProperty(productProperty));
        });

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductbvin() {
        return productbvin;
    }

    public void setProductbvin(String productbvin) {
        this.productbvin = productbvin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturerPartNumber() {
        return manufacturerPartNumber;
    }

    public void setManufacturerPartNumber(String manufacturerPartNumber) {
        this.manufacturerPartNumber = manufacturerPartNumber;
    }

    public boolean isHazardous() {
        return isHazardous;
    }

    public void setHazardous(boolean hazardous) {
        isHazardous = hazardous;
    }

    public String getCountryofOrigin() {
        return countryofOrigin;
    }

    public void setCountryofOrigin(String countryofOrigin) {
        this.countryofOrigin = countryofOrigin;
    }

    public String getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(String productWeight) {
        this.productWeight = productWeight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSku(String key) {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getListPrice() {
        return listPrice;
    }

    public void setListPrice(String listPrice) {
        this.listPrice = listPrice;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getStockMessage() {
        return stockMessage;
    }

    public void setStockMessage(String stockMessage) {
        this.stockMessage = stockMessage;
    }

    public String getSku() {
        return sku;
    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public Set<ProductProperty> getProperties() {
        return properties;
    }


    public void setProperties(Set<ProductProperty> properties) {
        this.properties = properties;
    }

    public void addProperty(ProductProperty productProperty){
        properties.add(productProperty);
        productProperty.setProduct(this);
    }

    public void removeProprety(ProductProperty productProperty){
        properties.remove(productProperty);
        productProperty.setProduct(null);
    }

    public String getMAPPrice() {
        return MAPPrice;
    }

    public void setMAPPrice(String MAPPrice) {
        this.MAPPrice = MAPPrice;
    }

    public double getNumPrice() {
        return numPrice;
    }

    public void setNumPrice(double numPrice) {
        this.numPrice = numPrice;
    }

    public int getQty() {

        if(this.stockMessage == null){
            return 0;
        }
        String[] substr = this.stockMessage.split(":");
        if (substr.length != 2) {
            return 0;
        }

        String strNumber = substr[1].trim();
        int qty = 0;
        try {
            qty = Integer.parseInt(strNumber);
        } catch (NumberFormatException e) { }

        return qty;

    }

    public void fillByParrent(Product parentProduct) {

        Class current = Product.class;

        Field[] fields = current.getDeclaredFields();
        for (Field field : fields) {

            if (field.getType() == Long.class || field.getType() == Boolean.class) {
                continue;
            }

            try {

                Object value = field.get(this);

                if (value == null || value.toString().isEmpty()) {
                   Object newValue = field.get(parentProduct);

                   if (newValue != null){
                       field.set(this, newValue);
                   }

                }

            }catch(IllegalAccessException e){
                e.printStackTrace();
            }

        }
    }
}
