package com.vkruk.parserkrollcorp.controller;

import com.vkruk.parserkrollcorp.entity.ParsingInfo;
import com.vkruk.parserkrollcorp.repository.ParsingInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class PagesController {

    private final ParsingInfoRepository parsingInfoRepository;

    @Autowired
    public PagesController(ParsingInfoRepository parsingInfoRepository) {
        this.parsingInfoRepository = parsingInfoRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String settings(Model model){
        List<ParsingInfo> allInfos = parsingInfoRepository.getLatestInfos();
        List<ParsingInfo> infos = allInfos.subList(Math.max(allInfos.size() - 30, 0), allInfos.size());
        model.addAttribute("infos", infos);
        return "index";
    }
}
