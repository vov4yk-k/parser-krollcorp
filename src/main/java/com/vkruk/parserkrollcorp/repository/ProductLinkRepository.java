package com.vkruk.parserkrollcorp.repository;

import com.vkruk.parserkrollcorp.entity.Product;
import com.vkruk.parserkrollcorp.entity.ProductLink;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductLinkRepository extends CrudRepository<ProductLink, Long> {

    List<ProductLink> findProductLinksByProductSKU(String sku);

    List<ProductLink> findProductLinksByProductSKUIn(List<String> productSKUList);

    @Query(value = "SELECT * FROM product_link WHERE productsku in (select parsing.sku from parsing where parsing.parsing_id = :parsingId)", nativeQuery = true)
    List<ProductLink> getProductLinksByParseId(@Param("parsingId") long parsingId);
}
