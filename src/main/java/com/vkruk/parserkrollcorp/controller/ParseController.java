package com.vkruk.parserkrollcorp.controller;

import com.vkruk.parserkrollcorp.services.ParserService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/parser")
public class ParseController {

    private ParserService parserService;
    private static final Logger logger = LogManager.getLogger(ParseController.class);

    @Autowired
    public ParseController(ParserService parserService) {
        this.parserService = parserService;
    }

    @GetMapping(value = "/parse_products{firstPage}{lastPage}{manufacturer}")
    public String parseProducts(int firstPage, int lastPage, String manufacturer){
        parserService.parseAll(firstPage, lastPage, manufacturer);
        return "ok";
    }

    @GetMapping(value = "/parse_links")
    public String parseLinks(){
        parserService.parseLinks();
        return "ok";
    }



}
