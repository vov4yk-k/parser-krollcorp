package com.vkruk.parserkrollcorp.repository;

import com.vkruk.parserkrollcorp.entity.ParsingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParsingInfoRepository extends JpaRepository<ParsingInfo, Long> {

    List<ParsingInfo> findParsingInfosByParsingId(long parsingId);

    List<ParsingInfo> findParsingInfosByParsingIdAndStatus(long parsingId, String status);
}
