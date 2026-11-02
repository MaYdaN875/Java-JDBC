package com.maydan.proyecto;
import com.maydan.proyecto.category.persistence.CategoryDao;
import com.maydan.proyecto.product.controller.ProductController;
import com.maydan.proyecto.product.exception.InvalidProductDataException;
import com.maydan.proyecto.product.interfaces.ProductRepository;
import com.maydan.proyecto.product.repository.ProductRepositoryServices;
import com.maydan.proyecto.product.service.ProductService;
import com.maydan.proyecto.product.view.ProductView;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        try{
            CategoryDao categoryDao = new CategoryDao();
            ProductRepository repositoryServices = new ProductRepositoryServices(categoryDao);
            ProductService productService = new ProductService(repositoryServices);
            ProductController controller = new ProductController(productService);
            ProductView view = new ProductView(controller);
            view.showMenu();
        }catch (SQLException | InvalidProductDataException e){
            System.out.println(e.getMessage());
        }


    }
}