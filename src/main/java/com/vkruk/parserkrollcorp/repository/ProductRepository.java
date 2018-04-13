package com.vkruk.parserkrollcorp.repository;

import com.vkruk.parserkrollcorp.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product,Long> {

    @Query(value = "select product.productbvin FROM products.product group by productbvin", nativeQuery = true)
    public List<String> getProductbvin();

    public List<Product> findProductsByProductbvin(String Productbvin);

    public List<Product> findProductsBySku(String sku);
}
