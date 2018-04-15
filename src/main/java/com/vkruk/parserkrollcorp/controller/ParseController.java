package com.vkruk.parserkrollcorp.controller;

import com.vkruk.parserkrollcorp.entity.ParsingInfo;
import com.vkruk.parserkrollcorp.services.ParserService;
import com.vkruk.parserkrollcorp.services.ParsingInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value="Parser", description="Parsing operations")
@RestController
@RequestMapping(value = "api/parser")
public class ParseController {

    private final ParserService parserService;
    private final ParsingInfoService parsingInfoService;
    private static final Logger logger = LogManager.getLogger(ParseController.class);

    @Autowired
    public ParseController(ParserService parserService, ParsingInfoService parsingInfoService) {
        this.parserService = parserService;
        this.parsingInfoService = parsingInfoService;
    }

    @ApiOperation(value = "Starts parsing session by search parameters.", response = Long.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Started parsing. Returns parsing session ID.", response = Long.class)})
    @PostMapping(value = "/parse_all")
    public long parseLinksAndProducts(String[] params){
        long parsingId = System.currentTimeMillis() / 1000L;
        parserService.parseLinksAndProducts(parsingId, params);
        return parsingId;
    }

    @ApiOperation(value = "Parsing progress information.")
    @GetMapping(value = "/parsing_info")
    public List<ParsingInfo> getParsingInformation(long parsingId){
        return parsingInfoService.getParsingInformation(parsingId);
    }
}
