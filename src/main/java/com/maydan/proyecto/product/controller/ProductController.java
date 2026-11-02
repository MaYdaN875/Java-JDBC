package com.maydan.proyecto.product.controller;

import com.maydan.proyecto.product.exception.InvalidProductDataException;
import com.maydan.proyecto.product.exception.ProductNotFoundException;
import com.maydan.proyecto.product.model.Product;
import com.maydan.proyecto.product.model.ProductCategory;
import com.maydan.proyecto.product.service.ProductService;
import com.maydan.proyecto.product.utils.Validates;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductController {
    private final ProductService services;

    public ProductController(ProductService services) {
        this.services = services;
    }

    public void addProduct(Product product) throws InvalidProductDataException, ProductNotFoundException, SQLException {
        Validates.validateObject(product, "El product no pueden ser nulo.");
        services.saveProduct(product);
    }

    public void removeProduct(Long id) throws InvalidProductDataException, ProductNotFoundException, SQLException {
        Validates.validate(id, "El id no puede ser nulo");
        services.deleteProduct(id);
    }

    public List<Product> getAllProducts() throws InvalidProductDataException {
        return services.getAllProducts();
    }
    public List<Product> getAllProductsByCategory(ProductCategory category) throws InvalidProductDataException {
        return services.getAllProductsByCategory(category);
    }

    public Optional<Product> getProductById(long id) throws InvalidProductDataException {
        Validates.validate(id, "El id no puede ser nulo");
        return services.getProductById(id);
    }

    public Optional<Product> getProductByIdDB(long id) throws InvalidProductDataException, SQLException {
        Validates.validate(id, "El id no puede ser nulo");
        return services.getProductByIdDB(id);
    }

    public void updateProduct(Product product) throws InvalidProductDataException, ProductNotFoundException, SQLException {
        Validates.validateObject(product, "El product no pueden ser nulo.");
        services.updateProduct(product);
    }
}
