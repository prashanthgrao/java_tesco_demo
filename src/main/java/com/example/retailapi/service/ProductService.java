package com.example.retailapi.service;

import com.example.retailapi.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final List<Product> productList = new ArrayList<>();
    private Long currentId = 1L;

    public List<Product> getAllProducts(Optional<String> name, Optional<BigDecimal> minPrice, Optional<BigDecimal> maxPrice,
                                        Optional<String> sortBy, Optional<Integer> page, Optional<Integer> pageSize) {
        return productList.stream()
                .filter(product -> name.map(n -> product.getName().toLowerCase().contains(n.toLowerCase())).orElse(true))
                .filter(product -> minPrice.map(min -> product.getPrice().compareTo(min) >= 0).orElse(true))
                .filter(product -> maxPrice.map(max -> product.getPrice().compareTo(max) <= 0).orElse(true))
                .sorted(sortBy.map(s -> s.equalsIgnoreCase("desc") ?
                        Comparator.comparing(Product::getPrice).reversed() : Comparator.comparing(Product::getPrice))
                        .orElse(Comparator.comparing(Product::getId)))
                .skip(page.map(p -> (long) (p - 1) * pageSize.orElse(10)).orElse(0L))
                .limit(pageSize.orElse(10))
                .collect(Collectors.toList());
    }

    public Product createProduct(Product product) {
        product.setId(currentId++);
        productList.add(product);
        return product;
    }

    // Find, update, and delete methods would go here
}
