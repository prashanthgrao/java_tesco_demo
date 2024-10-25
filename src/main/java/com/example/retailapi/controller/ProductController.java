package com.example.retailapi.controller;

import com.example.retailapi.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private List<Product> products = new ArrayList<>();

    public ProductController() {
        products.add(new Product(1, "Laptop", "Electronics", 999.99));
        products.add(new Product(2, "Sneakers", "Footwear", 59.99));
    }

    @GetMapping
    public List<Product> getAllProducts(
            @RequestParam Optional<String> category,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size) {
        
        List<Product> filteredProducts = category.isPresent()
                ? products.stream().filter(p -> p.getCategory().equalsIgnoreCase(category.get())).collect(Collectors.toList())
                : products;

        int pageSize = size.orElse(5);
        int pageNumber = page.orElse(0);
        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, filteredProducts.size());

        return filteredProducts.subList(start, end);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        Optional<Product> product = products.stream().filter(p -> p.getId() == id).findFirst();
        return product.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product) {
        products.add(product);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id, @Valid @RequestBody Product updatedProduct) {
        Optional<Product> product = products.stream().filter(p -> p.getId() == id).findFirst();
        
        if (product.isPresent()) {
            products.remove(product.get());
            updatedProduct.setId(id);
            products.add(updatedProduct);
            return ResponseEntity.ok(updatedProduct);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        Optional<Product> product = products.stream().filter(p -> p.getId() == id).findFirst();
        if (product.isPresent()) {
            products.remove(product.get());
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }
}
