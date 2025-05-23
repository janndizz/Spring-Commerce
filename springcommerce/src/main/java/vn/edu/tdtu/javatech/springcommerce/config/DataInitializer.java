package vn.edu.tdtu.javatech.springcommerce.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.javatech.springcommerce.model.*;
import vn.edu.tdtu.javatech.springcommerce.model.User.Role;
import vn.edu.tdtu.javatech.springcommerce.repository.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    private final BCryptPasswordEncoder passwordEncoder;


    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            // 1. Tạo Users
            User admin = new User(null, "admin", passwordEncoder.encode("admin123"), "admin@example.com", "0123456789", Role.ADMIN);
            User client = new User(null, "client", passwordEncoder.encode("client123"), "client@example.com", "0987654321", Role.CLIENT);
            User client1 = new User(null, "client1", passwordEncoder.encode("client123"), "client1@example.com", "0917654321", Role.CLIENT);

            userRepository.save(admin);
            userRepository.save(client);
            userRepository.save(client1);

            // 2. Tạo Products (đĩa CD)
            List<Product> products = List.of(
                    new Product(null, "Thriller", "Michael Jackson", "Pop", 15.99, "Epic", "Black", "Best-selling album of all time", "card-item1.jpg", 100),
                    new Product(null, "Back in Black", "AC/DC", "Rock", 13.99, "Atlantic", "Black", "Classic hard rock album", "card-image2.jpg", 80),
                    new Product(null, "The Dark Side of the Moon", "Pink Floyd", "Progressive Rock", 16.99, "Harvest", "Rainbow", "Iconic concept album", "card-image3.jpg", 90),
                    new Product(null, "Rumours", "Fleetwood Mac", "Rock", 12.50, "Warner Bros.", "Cream", "1977 Grammy-winning album", "card-image4.jpg", 60),
                    new Product(null, "21", "Adele", "Soul", 14.99, "XL Recordings", "Grey", "Award-winning soulful album", "card-image5.jpg", 70)
            );
            productRepository.saveAll(products);

            // 3. Tạo Cart và CartItems cho client
            Cart cart = new Cart();
            cart.setUser(client);
            List<CartItem> cartItems = new ArrayList<>();

            CartItem item1 = new CartItem(null, cart, products.get(0), 1);
            CartItem item2 = new CartItem(null, cart, products.get(2), 2);
            cartItems.add(item1);
            cartItems.add(item2);

            cart.setItems(cartItems);
            cartRepository.save(cart);  // cascade sẽ tự lưu CartItems

            // 4. Tạo Order và OrderItems
            Order order = new Order();
            order.setUser(client);
            order.setAddress("123 CD Street, Music City");
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(Order.Status.DELIVERED);

            List<OrderItem> orderItems = new ArrayList<>();
            orderItems.add(new OrderItem(null, order, products.get(1), 1, products.get(1).getPrice()));
            orderItems.add(new OrderItem(null, order, products.get(4), 2, products.get(4).getPrice()));
            order.setItems(orderItems);

            double total = orderItems.stream()
                    .mapToDouble(oi -> oi.getPrice() * oi.getQuantity())
                    .sum();
            order.setTotalAmount(total);

            orderRepository.save(order);  // cascade sẽ lưu OrderItems

            System.out.println("✅ Sample data seeded successfully.");
        }
    }
}
