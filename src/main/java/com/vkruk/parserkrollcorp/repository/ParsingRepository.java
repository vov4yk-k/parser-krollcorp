package com.vkruk.parserkrollcorp.repository;

import com.vkruk.parserkrollcorp.entity.Parsing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParsingRepository extends JpaRepository<Parsing,Long> {

}
