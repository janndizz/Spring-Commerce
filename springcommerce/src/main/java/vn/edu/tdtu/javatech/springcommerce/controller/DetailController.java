package vn.edu.tdtu.javatech.springcommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.edu.tdtu.javatech.springcommerce.model.Product;
import vn.edu.tdtu.javatech.springcommerce.service.ProductService;

@Controller
public class DetailController {
    @Autowired
    private ProductService productService;
    @GetMapping("detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "detail";
    }
}