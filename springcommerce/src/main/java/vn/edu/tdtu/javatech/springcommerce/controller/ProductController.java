package vn.edu.tdtu.javatech.springcommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.tdtu.javatech.springcommerce.service.ProductService;
import vn.edu.tdtu.javatech.springcommerce.model.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

//    @GetMapping("/products")
//    public String products(Model model) {
//        model.addAttribute("products", productService.getAllProducts());
//        return "product"; // products.html
//    }
    @GetMapping("/products")
    public String listProducts(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Model model) {

        List<Product> products = productService.filterProducts(genre, artist, brand, color, minPrice, maxPrice);

        model.addAttribute("products", products);
        model.addAttribute("genres", productService.getAllGenres());
        model.addAttribute("brands", productService.getAllBrands());
        model.addAttribute("colors", productService.getAllColors());
        // Giữ lại các giá trị filter đã chọn
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedArtist", artist);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedColor", color);
        model.addAttribute("selectedMinPrice", minPrice);
        model.addAttribute("selectedMaxPrice", maxPrice);

        return "product";
    }
}
