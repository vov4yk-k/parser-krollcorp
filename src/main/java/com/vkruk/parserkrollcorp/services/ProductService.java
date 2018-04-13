package com.vkruk.parserkrollcorp.services;

import com.vkruk.parserkrollcorp.entity.Product;
import com.vkruk.parserkrollcorp.model.response.ParentProductResponse;
import com.vkruk.parserkrollcorp.model.response.ProductResponse;
import com.vkruk.parserkrollcorp.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ParentProductResponse> getProducts(){

        ArrayList<ParentProductResponse> result = new ArrayList<>();

        List<String> bpvins = productRepository.getProductbvin();

        for (String productbpvin: bpvins) {

            List<Product> products = productRepository.findProductsByProductbvin(productbpvin);

            ArrayList<ProductResponse> childs = new ArrayList<>();

            if(products.size() > 1){
                products.forEach( product -> {
                    childs.add(new ProductResponse(product));
                });
            }

            ProductResponse[] childsArray = childs.toArray(new ProductResponse[childs.size()]);

            result.add(new ParentProductResponse(products.get(0), childsArray));

        }

        return result;
    }

}
