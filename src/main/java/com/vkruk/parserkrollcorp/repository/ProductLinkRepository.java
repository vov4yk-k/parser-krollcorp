package com.vkruk.parserkrollcorp.repository;

import com.vkruk.parserkrollcorp.entity.ProductLink;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLinkRepository extends CrudRepository<ProductLink, Long> {

}
