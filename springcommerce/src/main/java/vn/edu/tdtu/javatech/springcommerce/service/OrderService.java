package vn.edu.tdtu.javatech.springcommerce.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.javatech.springcommerce.model.Order.Status;
import vn.edu.tdtu.javatech.springcommerce.model.*;
import vn.edu.tdtu.javatech.springcommerce.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findByIdWithItems(id);
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAllWithItems();
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllWithItems();
    }

    @Transactional
    public void placeOrder(String username, String address) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        List<CartItem> cartItems = cartService.getUserCartItems(username);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống, không thể thanh toán.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);

        double totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            totalAmount += cartItem.getQuantity() * cartItem.getProduct().getPrice();
            orderItems.add(orderItem);

            // Update product quantity
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        orderRepository.save(order);  // cascade will persist OrderItem
        cartService.clearCart(user);
    }

    public List<Order> getOrdersByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return orderRepository.findByUserWithItems(user);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findByIdWithItems(orderId).orElse(null);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long id, Status status) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        // Nếu hủy đơn hàng, trả lại số lượng sản phẩm vào kho
        if (status == Status.CANCELLED && order.getStatus() != Status.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<Order> findOrdersByStatus(Status status) {
        return orderRepository.findByStatusWithItems(status);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        // Nếu đơn hàng chưa bị hủy, trả lại số lượng sản phẩm vào kho
        if (order.getStatus() != Status.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        orderRepository.delete(order);
    }
}