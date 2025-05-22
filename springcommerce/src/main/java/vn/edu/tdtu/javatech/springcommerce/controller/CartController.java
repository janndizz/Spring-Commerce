package vn.edu.tdtu.javatech.springcommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import vn.edu.tdtu.javatech.springcommerce.dto.CartItemRequest;
import vn.edu.tdtu.javatech.springcommerce.model.User;
import vn.edu.tdtu.javatech.springcommerce.service.CartService;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Hiển thị giỏ hàng
    @GetMapping("/cart")
    public String viewCart(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getUsername();
        User user = cartService.findUserByUsername(username);

        var cart = cartService.getCartForUser(user);
        model.addAttribute("cart", cart);
        model.addAttribute("items", cart.getItems());

        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        model.addAttribute("total", total);

        return "cart";
    }

    // API cập nhật số lượng sản phẩm
    @PostMapping("/api/cart/update")
    @ResponseBody
    public ResponseEntity<?> updateQuantity(@AuthenticationPrincipal UserDetails principal,
                                            @RequestBody CartItemRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = cartService.findUserByUsername(principal.getUsername());
        cartService.updateQuantity(user, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    // API xóa sản phẩm khỏi giỏ hàng
    @PostMapping("/api/cart/remove")
    @ResponseBody
    public ResponseEntity<?> removeItem(@AuthenticationPrincipal UserDetails principal,
                                        @RequestBody CartItemRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = cartService.findUserByUsername(principal.getUsername());
        cartService.removeItem(user, request.getProductId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cart/add")
    public String addToCart(@AuthenticationPrincipal UserDetails principal,
                            @RequestParam("productId") Long productId,
                            @RequestParam("quantity") int quantity) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = cartService.findUserByUsername(principal.getUsername());
        cartService.addToCart(user, productId, quantity);

        return "redirect:/cart?success=true";
    }

}
