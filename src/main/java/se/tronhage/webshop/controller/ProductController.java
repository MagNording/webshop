package se.tronhage.webshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import se.tronhage.webshop.entity.Product;
import se.tronhage.webshop.repository.ProductRepo;

import java.util.List;

@Controller
public class ProductController {

    private final ProductRepo productRepo;

    @Autowired
    public ProductController(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @GetMapping("/products")
    public String listProducts(Model m){
        List<Product>products = productRepo.findAll();
        m.addAttribute("products",products);
        return "products";
    }
}