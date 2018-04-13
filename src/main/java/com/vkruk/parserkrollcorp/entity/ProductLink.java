package com.vkruk.parserkrollcorp.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class ProductLink {

    private @Id @GeneratedValue long id;
    private String productSKU;
    private String link;

    private ProductLink(){}

    public ProductLink(String productSKU, String link) {
        this.productSKU = productSKU;
        this.link = link;
    }

    public String getProductSKU() {
        return productSKU;
    }

    public String getLink() {
        return link;
    }
}
