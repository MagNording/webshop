package se.nording.webshop.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.nording.webshop.entity.Product;
import se.nording.webshop.services.ProductService;
import se.nording.webshop.repository.ProductRepo;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    private final ProductRepo productRepo;
    private final ProductService productService;

    @Autowired
    public ProductController(ProductRepo productRepo, ProductService productService) {
        this.productRepo = productRepo;
        this.productService = productService;
    }

    @GetMapping("/addnewproduct")
    public String addProduct(Model m){
        m.addAttribute("product", new Product());
        return "addnewproduct";
    }

    @PostMapping("/addnewproduct")
    public String addProduct(Model m, @Valid @ModelAttribute("product") Product product,
                             BindingResult result) {
        if (result.hasErrors()) {
            return "addnewproduct";
        }
        try {
            productService.createNewProduct(product.getName(),product.getCategory(),product.getDescription(),product.getPrice());
            m.addAttribute("registrationSuccess",true);
        } catch (Exception e) {
            m.addAttribute("product", new Product());
            m.addAttribute("errorMessage","Something went wrong..");
            return "addnewproduct";

        }
        return "redirect:/products";
    }


    @GetMapping("/products")
    public String listProducts(@RequestParam(name="category", required = false,defaultValue = "all") String category, Model m) {
        List<Product> products = productRepo.findAll();

        switch(category){
            case "putter" -> products = productService.findAllPutters();

            case "midrange" -> products = productService.findAllMidrange();

            case "distance_driver" -> products =  productService.findAllDrivers();

            case "all" -> products = productService.findAllProducts();
        }
        m.addAttribute("products", products);
        return "products";
    }

    @PostMapping("/products")
    public String listProductsPost(@RequestParam(name="category", required = false,defaultValue = "all") String category){
        return "redirect:/products?category=" + category;
    }

    @GetMapping("/webshop")
    public String allProducts(@RequestParam(name="category", required = false,defaultValue = "all") String category, Model m) {
        List<Product> products = productRepo.findAll();

        switch(category){
            case "putter" -> products = productService.findAllPutters();

            case "midrange" -> products = productService.findAllMidrange();

            case "distance_driver" -> products =  productService.findAllDrivers();

            case "all" -> products = productService.findAllProducts();
        }
        m.addAttribute("products", products);
        return "webshop";
    }

    @PostMapping("/webshop")
    public String allProductsPost(@RequestParam(name="category", required = false,defaultValue = "all") String category){
        return "redirect:/webshop?category=" + category;
    }

    @GetMapping("/editproduct")
    public String editProduct(@RequestParam("productid") Long productid, Model model){
        Optional<Product> productOptional = productRepo.findById(productid);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            model.addAttribute("product", product);
            return "editproduct";
        } else {
            return "errorPage";
        }
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("query") String query, Model model) {
        List<Product> searchResults = productService.searchProducts(query);
        model.addAttribute("searchResults", searchResults);
        return "webshop";
    }

    @PostMapping("/editproduct")
    public String editProduct(@RequestParam("product") Product product, Model m){
        try {
            productService.updateProduct(product);
            m.addAttribute("registrationSuccess","Product updated successfully");
        }catch(Exception e){
            m.addAttribute("errorMessage","Error updating product.");
        }
        return "editproduct";
    }
}