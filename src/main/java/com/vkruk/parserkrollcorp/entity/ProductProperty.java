package com.vkruk.parserkrollcorp.entity;

import javax.persistence.*;

//@Data
@Entity(name = "ProductProperty")
@Table(name = "product_property")
public class ProductProperty {

    private @Id @GeneratedValue long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    private String productbvin;
    private String code;
    private String name;
    private String valueCode;
    private String valueName;

    private ProductProperty() {
    }

    public ProductProperty(String code){
        this.code = code;
    }

    public ProductProperty(String productbvin, String code, String name, String valueCode, String valueName) {
        this.productbvin = productbvin;
        this.code = code;
        this.name = name;
        this.valueCode = valueCode;
        this.valueName = valueName;
    }

    public ProductProperty(ProductProperty productProperty){
        this.productbvin  = productProperty.getProductbvin();
        this.code = productProperty.getCode();
        this.name = productProperty.getName();
        this.valueCode = productProperty.getValueCode();
        this.valueName = productProperty.getValueName();
    }

    public String getProductbvin() {
        return productbvin;
    }

    public void setProductbvin(String productbvin) {
        this.productbvin = productbvin;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValueCode() {
        return valueCode;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getParameter(){
        return code+": "+valueCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
