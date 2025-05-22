package vn.edu.tdtu.javatech.springcommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.tdtu.javatech.springcommerce.model.*;
import vn.edu.tdtu.javatech.springcommerce.repository.*;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
//    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Lấy giỏ hàng của người dùng. Nếu chưa có thì tạo mới.
     */
    public Cart getCartForUser(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @Transactional
    public void addToCart(User user, Long productId, int quantity) {
        Cart cart = getCartForUser(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    @Transactional
    public void updateQuantity(User user, Long productId, int quantity) {
        Cart cart = getCartForUser(user);

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    if (quantity <= 0) {
                        cart.getItems().remove(item); // Nếu số lượng <= 0 thì xóa luôn
                    } else {
                        item.setQuantity(quantity);   // Cập nhật số lượng
                    }
                });

        cartRepository.save(cart);
    }
    @Transactional
    public void removeItem(User user, Long productId) {
        Cart cart = getCartForUser(user);

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        cartRepository.save(cart);
    }



    /**
     * Lấy User từ username (dành cho @AuthenticationPrincipal)
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
