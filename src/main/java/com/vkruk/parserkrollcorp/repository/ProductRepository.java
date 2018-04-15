package com.vkruk.parserkrollcorp.repository;

import com.vkruk.parserkrollcorp.entity.Product;
import com.vkruk.parserkrollcorp.entity.ProductLink;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product,Long> {

    @Query(value = "select product.productbvin FROM product group by productbvin", nativeQuery = true)
    List<String> getProductbvin();

    List<Product> findProductsByProductbvin(String Productbvin);

    List<Product> findProductsBySku(String sku);

    @Query(value =  "SELECT product.productbvin " +
                    "FROM product " +
                    "WHERE sku IN (select parsing.sku from parsing where parsing.parsing_id = :parsingId) " +
                    "Group By productbvin", nativeQuery = true)
    List<String> getProductbvinListByParseId(@Param("parsingId") long parsingId);

    @Query(value =  "SELECT * " +
                    "FROM product " +
                    "WHERE sku IN (select parsing.sku from parsing where parsing.parsing_id = :parsingId) ", nativeQuery = true)
    List<Product> getProductsByParseId(@Param("parsingId") long parsingId);
}
