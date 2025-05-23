package vn.edu.tdtu.javatech.springcommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.javatech.springcommerce.service.*;
import vn.edu.tdtu.javatech.springcommerce.repository.*;
import vn.edu.tdtu.javatech.springcommerce.model.*;

import java.security.Principal;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/checkout")
    public String showCheckoutForm(Model model, Principal principal) {
        String username = principal.getName();

        List<CartItem> items = cartService.getUserCartItems(username);
        double total = cartService.calculateTotal(items);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("userFullName", user.getUsername());  // hoặc fullname nếu có
        model.addAttribute("userPhone", user.getPhonenumber());

        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@RequestParam("address") String address,
                                  Principal principal,
                                  Model model) {
        String username = principal.getName();
        try {
            orderService.placeOrder(username, address);
            return "order_success";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "checkout";
        }
    }


    @GetMapping("/orders")
    public String viewOrders(Model model, Principal principal) {
        String username = principal.getName();
        List<Order> orders = orderService.getOrdersByUser(username);
        System.out.println("Orders found: " + orders.size()); // kiểm tra số đơn hàng
        model.addAttribute("orders", orders);
        return "orders";
    }


}
