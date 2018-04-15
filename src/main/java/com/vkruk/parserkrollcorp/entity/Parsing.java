package com.vkruk.parserkrollcorp.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity(name = "Parsing")
@Table(name = "parsing")
public class Parsing {
    private @Id @GeneratedValue long id;
    private long parsingId;
    private String sku;

    private Parsing(){
    }

    public Parsing(long parsingId, String sku) {
        this.parsingId = parsingId;
        this.sku = sku;
    }

    public Parsing(long id, long parsingId, String sku) {
        this.id = id;
        this.parsingId = parsingId;
        this.sku = sku;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParsingId() {
        return parsingId;
    }

    public void setParsingId(long parsingId) {
        this.parsingId = parsingId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
