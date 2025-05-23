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
                    // Lana Del Rey albums
                    new Product(null, "Born To Die", "Lana Del Rey", "Pop", 990000, "Interscope", "Blue", "Debut studio album with hit singles like 'Video Games'", "lana_born_to_die.jpg", 50),
                    new Product(null, "Norman F. Rockwell!", "Lana Del Rey", "Alternative", 1090000, "Polydor", "White", "Critically acclaimed sixth studio album", "lana_nfr.jpg", 45),

                    // The Weeknd albums
                    new Product(null, "After Hours", "The Weeknd", "R&B", 790000, "Republic", "Red", "Includes hits like 'Blinding Lights' and 'Save Your Tears'", "weeknd_after_hours.jpg", 60),
                    new Product(null, "Dawn FM", "The Weeknd", "Pop", 590000, "XO", "Purple", "Concept album with 80s synth-pop influences", "weeknd_dawn_fm.jpg", 55),

                    // Taylor Swift albums
                    new Product(null, "1989", "Taylor Swift", "Pop", 890000, "Big Machine", "Pastel", "Grammy-winning pop transformation album", "taylor_1989.jpg", 70),
                    new Product(null, "Folklore", "Taylor Swift", "Indie Folk", 490000, "Republic", "Grey", "Surprise pandemic album with indie aesthetic", "taylor_folklore.jpg", 65),

                    // Billie Eilish albums
                    new Product(null, "Where Do We Go?", "Billie Eilish", "Alternative", 720000, "Darkroom", "Green", "Debut studio album with dark pop sound", "billie_wwafawdwg.jpg", 75),
                    new Product(null, "Happier Than Ever", "Billie Eilish", "Pop", 1190000, "Interscope", "Pink", "Sophomore album exploring fame and relationships", "billie_happier.jpg", 60),

                    // Ariana Grande albums
                    new Product(null, "Thank U, Next", "Ariana Grande", "Pop", 2090000, "Republic", "Peach", "Breakup anthem album with R&B influences", "ariana_tun.jpg", 80),
                    new Product(null, "Positions", "Ariana Grande", "R&B", 950000, "Republic", "Lavender", "Album with sensual R&B and trap elements", "ariana_positions.jpg", 65),

                    // BlackPink albums
                    new Product(null, "The Album", "BlackPink", "K-Pop", 720000, "YG Entertainment", "Black", "First Korean full-length studio album", "blackpink_the_album.jpg", 90),
                    new Product(null, "Born Pink", "BlackPink", "K-Pop", 890000, "YG Entertainment", "Pink", "Second Korean studio album with global hits", "blackpink_born_pink.jpg", 85),
                    new Product(null, "RUBY", "Jennie (BlackPink)", "K-Pop", 1290000, "YG Entertainment", "Ruby Red", "Jennie's highly anticipated solo album with edgy concepts", "jennie_ruby.jpg", 100)
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
