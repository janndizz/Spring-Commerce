package vn.edu.tdtu.javatech.springcommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.tdtu.javatech.springcommerce.service.ProductService;

@Controller
public class ClientController {

    @Autowired
    private ProductService productService;

    @GetMapping("/client")
    public String index(Model model) {
        model.addAttribute("products", productService.getFeaturedProducts());
        model.addAttribute("lastedProducts", productService.getLastedProducts());
        return "client/index"; // trỏ đến file templates/index.html
    }
}
