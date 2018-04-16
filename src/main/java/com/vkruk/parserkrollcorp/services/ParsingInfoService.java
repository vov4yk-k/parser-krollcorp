package com.vkruk.parserkrollcorp.services;


import com.vkruk.parserkrollcorp.entity.ParsingInfo;
import com.vkruk.parserkrollcorp.repository.ParsingInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
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
        parsingInfoRepository.saveAndFlush(new ParsingInfo(parsingId,"InProgress", description));
    }

    public void error(long parsingId, String description){
        parsingInfoRepository.saveAndFlush(new ParsingInfo(parsingId,"Error", description));
    }

    public void finished(long parsingId, String description){
        parsingInfoRepository.saveAndFlush(new ParsingInfo(parsingId,"Finished", description));
    }

    public void inProgressUpdate(long parsingId, String description){
        List<ParsingInfo> infos = parsingInfoRepository.findParsingInfosByParsingIdAndStatus(parsingId,"InProgress");
        if(infos.isEmpty()){
            parsingInfoRepository.saveAndFlush(new ParsingInfo(parsingId,"InProgress", description));
        }else {
            ParsingInfo parsingInfo = infos.get(infos.size()-1);
            parsingInfo.setDate(new Date());
            parsingInfo.setDescription(description);
            parsingInfoRepository.saveAndFlush(parsingInfo);
        }
    }

    public double minutesForAllCurrentOperations(long parsingId){

        long duration = 0;

        ZoneId defaultZoneId = ZoneId.systemDefault();

        List<ParsingInfo> infos = parsingInfoRepository.findParsingInfosByParsingId(parsingId);
        if(infos.isEmpty()){
            duration = 0;
        }else if(infos.size()==1){
            Instant startDate = infos.get(0).getDate().toInstant();
            Instant enddate = (new Date()).toInstant();
            duration = Duration.between(startDate.atZone(defaultZoneId), enddate.atZone(defaultZoneId)).toMillis();
        }else{
            Instant startDate = infos.get(0).getDate().toInstant();
            Instant enddate = infos.get(infos.size()-1).getDate().toInstant();
            duration = Duration.between(startDate.atZone(defaultZoneId), enddate.atZone(defaultZoneId)).toMillis();
        }

        return (double) duration/60000;
    }

}
