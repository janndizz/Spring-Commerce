package vn.edu.tdtu.javatech.springcommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @GetMapping("/cart")
    public String Cart() {
        return "cart";
    }
}
