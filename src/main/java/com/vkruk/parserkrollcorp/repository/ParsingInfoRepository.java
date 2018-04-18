package com.vkruk.parserkrollcorp.repository;

import com.vkruk.parserkrollcorp.entity.ParsingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParsingInfoRepository extends JpaRepository<ParsingInfo, Long> {

    List<ParsingInfo> findParsingInfosByParsingId(long parsingId);

    List<ParsingInfo> findParsingInfosByParsingIdAndStatus(long parsingId, String status);

    @Query(value = "SELECT * FROM parsing_info " +
                    "WHERE (parsing_id, date, id) in (SELECT parsing_id, Max(date), Max(id) as date " +
                    "FROM parsing_info Group By parsing_id)", nativeQuery = true)
    List<ParsingInfo> getLatestInfos();

}
