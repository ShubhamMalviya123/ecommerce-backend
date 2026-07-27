package com.ecommerce.backend.config;

import com.ecommerce.backend.entity.*;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ---- Default admin ----
        if (!userRepository.existsByEmail("admin@shopease.com")) {
            User admin = new User();
            admin.setFullName("Admin");
            admin.setEmail("admin@shopease.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin);
            System.out.println(">>> Default admin created: admin@shopease.com / admin123");
        }

        // ---- Sample categories + products (only if DB empty) ----
        if (categoryRepository.count() == 0) {
            Category electronics = categoryRepository.save(new Category(null, "Electronics", "Gadgets and devices"));
            Category fashion = categoryRepository.save(new Category(null, "Fashion", "Clothing and accessories"));
            Category home = categoryRepository.save(new Category(null, "Home & Kitchen", "Home essentials"));

            saveProduct("Wireless Headphones", "Noise-cancelling over-ear headphones", "2999", "https://picsum.photos/seed/headphones/400/400", 50, electronics);
            saveProduct("Smart Watch", "Fitness tracking smart watch", "3499", "https://picsum.photos/seed/smartwatch/400/400", 30, electronics);
            saveProduct("Bluetooth Speaker", "Portable speaker with deep bass", "1599", "https://picsum.photos/seed/speaker/400/400", 40, electronics);
            saveProduct("Men's Casual Shirt", "100% cotton casual shirt", "899", "https://picsum.photos/seed/shirt/400/400", 100, fashion);
            saveProduct("Women's Handbag", "Leather handbag", "1799", "https://picsum.photos/seed/handbag/400/400", 60, fashion);
            saveProduct("Running Shoes", "Lightweight running shoes", "2499", "https://picsum.photos/seed/shoes/400/400", 70, fashion);
            saveProduct("Non-stick Cookware Set", "5-piece non-stick cookware set", "2199", "https://picsum.photos/seed/cookware/400/400", 25, home);
            saveProduct("LED Table Lamp", "Adjustable brightness LED lamp", "699", "https://picsum.photos/seed/lamp/400/400", 80, home);

            System.out.println(">>> Sample categories and products seeded");
        }
    }

    private void saveProduct(String name, String desc, String price, String img, int stock, Category category) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(new BigDecimal(price));
        p.setImageUrl(img);
        p.setStock(stock);
        p.setCategory(category);
        productRepository.save(p);
    }
}
