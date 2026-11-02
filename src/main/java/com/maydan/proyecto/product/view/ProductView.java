package com.maydan.proyecto.product.view;

import com.maydan.proyecto.category.model.Category;
import com.maydan.proyecto.product.controller.ProductController;
import com.maydan.proyecto.product.exception.InvalidProductDataException;
import com.maydan.proyecto.product.exception.ProductNotFoundException;
import com.maydan.proyecto.product.model.Product;
import com.maydan.proyecto.product.model.ProductCategory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ProductView {
    private final ProductController productController;
    private final Scanner scanner;

    public ProductView(ProductController productController) {
        this.productController = productController;
        scanner = new Scanner(System.in);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Agregar Producto");
            System.out.println("2. Mostrar Productos");
            System.out.println("3. Buscar Producto por ID");
            System.out.println("4. Eliminar Producto por ID");
            System.out.println("5. Modificar Producto por ID");
            System.out.println("6. Salir");
            System.out.print("Opción: ");

            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1 -> addProductView();
                case 2 -> showAllView();
                case 3 -> findByIdView();
                case 4 -> deleteProductView();
                case 5 -> updateProductView();
                case 6 -> {
                    scanner.close();
                    return;
                }
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }

    private void addProductView(){
        try {
            String name = readNonEmptyString("Ingrese el nombre del producto:");
            double price = readValidDouble("Ingrese un precio válido", 0);
            int stock = readValidInteger("Ingrese el stock del producto:", 0);
            String categoryName = readNonEmptyString("Ingrese el nombre de la categoria");
            Category category = new Category(categoryName.trim().toUpperCase());
            Product product = new Product(name, price, stock, category);
            productController.addProduct(product);
        } catch (InvalidProductDataException | ProductNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void findByIdView(){
        try {
            long id = readValidLong("Ingrese el ID del producto a buscar:", 1);
            Optional<Product> productOptional = productController.getProductById(id);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                showProduct(product);
            } else {
                System.out.println("No se encontró ningún producto con el ID: " + id);
            }
        } catch (InvalidProductDataException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteProductView(){
        try {
            long id = readValidLong("Ingrese el ID del producto a buscar:", 1);
            productController.removeProduct(id);
        } catch (InvalidProductDataException | ProductNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void updateProductView(){
        try {
            long id = readValidLong("Ingrese el ID del producto:", 0);
            Optional<Product> productOptional = productController.getProductByIdDB(id);
            if(productOptional.isPresent()){
                System.out.println("Producto a MODIFICAR");
                showProduct(productOptional.get());
                System.out.println("Seleccione el campo que desea modificar");
                System.out.println("1. Nombre");
                System.out.println("2. Precio");
                System.out.println("3. Stock");
                System.out.println("4. Categoría");
                System.out.println("5. TODOS");
                System.out.println("6. Salir");
                int option = scanner.nextInt();
                scanner.nextLine();
                switch (option){
                    case 1 -> productOptional.get().setName(readNonEmptyString("Ingrese el nombre del producto:"));
                    case 2 -> productOptional.get().setPrice(readValidDouble("Ingrese un precio válido", 0));
                    case 3 -> productOptional.get().setStock(readValidInteger("Ingrese el stock del producto:", 0));
                    case 4 -> {
                            String categoryName = readNonEmptyString("Ingrese el nombre de la categoria");
                            Category category = new Category(categoryName.trim().toUpperCase());
                            productOptional.get().setCategory(category);
                    }
                    case 5 -> {
                        productOptional.get().setName(readNonEmptyString("Ingrese el nombre del producto:"));
                        productOptional.get().setPrice(readValidDouble("Ingrese un precio válido", 0));
                        productOptional.get().setStock(readValidInteger("Ingrese el stock del producto:", 0));
                        String categoryName = readNonEmptyString("Ingrese el nombre de la categoria");
                        Category category = new Category(categoryName.trim().toUpperCase());
                        productOptional.get().setCategory(category);
                    }
                    case 6 -> {
                        return;
                    }
                }
                productController.updateProduct(productOptional.get());
            }else {
                System.out.println("El producto no existe...");
            }
        } catch (InvalidProductDataException | ProductNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private void showAllView() {
        try {
            List<Product> products = productController.getAllProducts();
            products.forEach(this::showProduct);
        } catch (InvalidProductDataException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showAllByCategoryView() {
        System.out.println("\n--- Buscar Productos por Categoría ---");
        try {
            String categoryIngresada = scanner.nextLine().trim().toUpperCase();
            ProductCategory category = ProductCategory.valueOf(categoryIngresada);
            List<Product> products = productController.getAllProductsByCategory(category);
            if (products.isEmpty()) {
                System.out.println("No hay productos registrados.");
            } else {
                products.forEach(this::showProduct);
            }
        } catch (InvalidProductDataException e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
    }

    private void showProduct(Product product){
        System.out.println("\n🔹 Producto:");
        System.out.println("Id: " + product.getId());
        System.out.println("Nombre: " + product.getName());
        System.out.println("Precio: " + product.getPrice());
        System.out.println("Stock: " + product.getStock());
        System.out.println("Categoría: " + product.getCategory());
        System.out.println("_________________________");
    }

    // Método para leer cadenas no vacías
    private String readNonEmptyString(String message) {
        String input;
        do {
            System.out.println(message);
            input = scanner.nextLine().trim();
            if (input.length() < 3) {
                System.out.println("El valor no puede estar vacío o el nombre es muy corto.");
            }
        } while (input.length() < 3);
        return input;
    }

    // Método para leer enteros válidos con un mínimo opcional
    private long readValidLong(String message, long min) {
        long value;
        do {
            System.out.println(message);
            String input = scanner.nextLine().trim();
            try {
                value = Long.parseLong(input);
                if (value < min) {
                    System.out.println("Debe ser al menos " + min + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        } while (true);
    }

    private int readValidInteger(String message, int min) {
        int value;
        do {
            System.out.println(message);
            String input = scanner.nextLine().trim();
            try {
                value = Integer.parseInt(input);
                if (value < min) {
                    System.out.println("Debe ser al menos " + min + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        } while (true);
    }

    private double readValidDouble(String message, double min) {
        double value;
        do {
            System.out.println(message);
            String input = scanner.nextLine().trim();
            try {
                value = Double.parseDouble(input);
                if (value < min) {
                    System.out.println("Debe ser al menos " + min + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        } while (true);
    }

    // Método sobrecargado para leer enteros sin restricciones
    private int readValidInteger() {
        return readValidInteger(" Ingrese un número:", Integer.MIN_VALUE);
    }
}
