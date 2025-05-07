package vn.edu.tdtu.javatech.springcommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.tdtu.javatech.springcommerce.service.ProductService;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", productService.getFeaturedProducts());
        model.addAttribute("lastedProducts", productService.getLastedProducts());
        return "index"; // trỏ đến file templates/index.html
    }
}
