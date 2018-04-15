package com.vkruk.parserkrollcorp.entity;

import com.vkruk.parserkrollcorp.repository.ParsingRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Date;

@Data
@Entity(name = "ParsingInfo")
public class ParsingInfo {

    private @Id @GeneratedValue long id;
    private Date date;
    private long parsingId;
    private String status;
    private @Lob String description;

    private ParsingInfo(){

    }

    public ParsingInfo(long parsingId, String status, String description) {
        this.date = new Date();
        this.parsingId = parsingId;
        this.status = status;
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getParsingId() {
        return parsingId;
    }

    public void setParsingId(long parsingId) {
        this.parsingId = parsingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}