package com.vkruk.parserkrollcorp.services;


import com.vkruk.parserkrollcorp.entity.ParsingInfo;
import com.vkruk.parserkrollcorp.repository.ParsingInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ParsingInfoService {

    private final ParsingInfoRepository parsingInfoRepository;

    @Autowired
    public ParsingInfoService(ParsingInfoRepository parsingInfoRepository) {
        this.parsingInfoRepository = parsingInfoRepository;
    }

    public List<ParsingInfo> getParsingInformation(long parseId){
        return parsingInfoRepository.findParsingInfosByParsingId(parseId);
    }

    public void inProgress(long parsingId, String description){
        parsingInfoRepository.save(new ParsingInfo(parsingId,"InProgress", description));
    }

    public void error(long parsingId, String description){
        parsingInfoRepository.save(new ParsingInfo(parsingId,"Error", description));
    }

    public void finished(long parsingId, String description){
        parsingInfoRepository.save(new ParsingInfo(parsingId,"Finished", description));
    }

    public void inProgressUpdate(long parsingId, String description){
        List<ParsingInfo> infos = parsingInfoRepository.findParsingInfosByParsingIdAndStatus(parsingId,"InProgress");
        if(infos.isEmpty()){
            parsingInfoRepository.save(new ParsingInfo(parsingId,"InProgress", description));
        }else {
            ParsingInfo parsingInfo = infos.get(infos.size()-1);
            parsingInfo.setDate(new Date());
            parsingInfo.setDescription(description);
            parsingInfoRepository.save(parsingInfo);
        }
    }

}
