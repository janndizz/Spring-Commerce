package vn.edu.tdtu.javatech.springcommerce.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.tdtu.javatech.springcommerce.model.*;
import vn.edu.tdtu.javatech.springcommerce.repository.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product1;
    private Product product2;
    private CartItem cartItem1;
    private CartItem cartItem2;
    private Order order;
    private OrderItem orderItem1;
    private OrderItem orderItem2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        product1 = new Product();
        product1.setId(1L);
        product1.setPrice(100.0);
        product1.setQuantity(10);

        product2 = new Product();
        product2.setId(2L);
        product2.setPrice(200.0);
        product2.setQuantity(5);

//        cartItem1 = new CartItem();
//        cartItem1.setId(1L);
//        cartItem1.setProduct(product1);
//        cartItem1.setQuantity(2);
//        cartItem1.setUser(user);
//
//        cartItem2 = new CartItem();
//        cartItem2.setId(2L);
//        cartItem2.setProduct(product2);
//        cartItem2.setQuantity(1);
//        cartItem2.setUser(user);

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setTotalAmount(400.0); // 2*100 + 1*200

        orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setOrder(order);
        orderItem1.setProduct(product1);
        orderItem1.setQuantity(2);
        orderItem1.setPrice(100.0);

        orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setOrder(order);
        orderItem2.setProduct(product2);
        orderItem2.setQuantity(1);
        orderItem2.setPrice(200.0);

        order.setItems(Arrays.asList(orderItem1, orderItem2));
    }

    @Test
    void findOrderById_WhenOrderExists_ShouldReturnOrder() {
        // Arrange
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        // Act
        Optional<Order> foundOrder = orderService.findOrderById(1L);

        // Assert
        assertTrue(foundOrder.isPresent());
        assertEquals(1L, foundOrder.get().getId());
        assertEquals(2, foundOrder.get().getItems().size());
        verify(orderRepository, times(1)).findByIdWithItems(1L);
    }

    @Test
    void findOrderById_WhenOrderNotExists_ShouldReturnEmpty() {
        // Arrange
        when(orderRepository.findByIdWithItems(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Order> foundOrder = orderService.findOrderById(99L);

        // Assert
        assertFalse(foundOrder.isPresent());
        verify(orderRepository, times(1)).findByIdWithItems(99L);
    }

    @Test
    void findAllOrders_ShouldReturnAllOrders() {
        // Arrange
        when(orderRepository.findAllWithItems()).thenReturn(Arrays.asList(order));

        // Act
        List<Order> orders = orderService.findAllOrders();

        // Assert
        assertEquals(1, orders.size());
        assertEquals(1L, orders.get(0).getId());
        verify(orderRepository, times(1)).findAllWithItems();
    }

    @Test
    @Transactional
    void placeOrder_WithValidCart_ShouldCreateOrder() {
        // Arrange
        String username = "testuser";
        String address = "123 Test Street";
        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(cartService.getUserCartItems(username)).thenReturn(cartItems);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderService.placeOrder(username, address);

        // Assert
        verify(userRepository, times(1)).findByUsername(username);
        verify(cartService, times(1)).getUserCartItems(username);

        // Verify product quantities are updated
        assertEquals(8, product1.getQuantity()); // 10 - 2
        assertEquals(4, product2.getQuantity()); // 5 - 1

        verify(productRepository, times(2)).save(any(Product.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartService, times(1)).clearCart(user);
    }

    @Test
    @Transactional
    void placeOrder_WithEmptyCart_ShouldThrowException() {
        // Arrange
        String username = "testuser";
        String address = "123 Test Street";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(cartService.getUserCartItems(username)).thenReturn(Collections.emptyList());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.placeOrder(username, address);
        });

        assertEquals("Giỏ hàng trống, không thể thanh toán.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(cartService, times(1)).getUserCartItems(username);
        verifyNoInteractions(productRepository, orderRepository);
    }

    @Test
    void getOrdersByUser_ShouldReturnUserOrders() {
        // Arrange
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(orderRepository.findByUserWithItems(user)).thenReturn(Arrays.asList(order));

        // Act
        List<Order> orders = orderService.getOrdersByUser(username);

        // Assert
        assertEquals(1, orders.size());
        assertEquals(1L, orders.get(0).getId());
        verify(userRepository, times(1)).findByUsername(username);
        verify(orderRepository, times(1)).findByUserWithItems(user);
    }

    @Test
    void getOrderById_WhenOrderExists_ShouldReturnOrder() {
        // Arrange
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        // Act
        Order foundOrder = orderService.getOrderById(1L);

        // Assert
        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getId());
        verify(orderRepository, times(1)).findByIdWithItems(1L);
    }

    @Test
    void getOrderById_WhenOrderNotExists_ShouldReturnNull() {
        // Arrange
        when(orderRepository.findByIdWithItems(99L)).thenReturn(Optional.empty());

        // Act
        Order foundOrder = orderService.getOrderById(99L);

        // Assert
        assertNull(foundOrder);
        verify(orderRepository, times(1)).findByIdWithItems(99L);
    }

    @Test
    void saveOrder_ShouldReturnSavedOrder() {
        // Arrange
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order savedOrder = orderService.saveOrder(order);

        // Assert
        assertNotNull(savedOrder);
        assertEquals(1L, savedOrder.getId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @Transactional
    void updateOrderStatus_ToCancelled_ShouldRestoreProductQuantities() {
        // Arrange
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order updatedOrder = orderService.updateOrderStatus(1L, Order.Status.CANCELLED);

        // Assert
        assertEquals(Order.Status.CANCELLED, updatedOrder.getStatus());

        // Verify product quantities are restored
        assertEquals(12, product1.getQuantity()); // 10 + 2
        assertEquals(6, product2.getQuantity()); // 5 + 1

        verify(productRepository, times(2)).save(any(Product.class));
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @Transactional
    void updateOrderStatus_ToShipped_ShouldNotRestoreQuantities() {
        // Arrange
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order updatedOrder = orderService.updateOrderStatus(1L, Order.Status.DELIVERED);

        // Assert
        assertEquals(Order.Status.DELIVERED, updatedOrder.getStatus());

        // Verify product quantities are not changed
        assertEquals(10, product1.getQuantity());
        assertEquals(5, product2.getQuantity());

        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void findOrdersByStatus_ShouldReturnFilteredOrders() {
        // Arrange
        when(orderRepository.findByStatusWithItems(Order.Status.PENDING))
                .thenReturn(Arrays.asList(order));

        // Act
        List<Order> orders = orderService.findOrdersByStatus(Order.Status.PENDING);

        // Assert
        assertEquals(1, orders.size());
        assertEquals(Order.Status.PENDING, orders.get(0).getStatus());
        verify(orderRepository, times(1)).findByStatusWithItems(Order.Status.PENDING);
    }

    @Test
    @Transactional
    void deleteOrder_NotCancelled_ShouldRestoreProductQuantities() {
        // Arrange
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).delete(order);

        // Act
        orderService.deleteOrder(1L);

        // Assert
        // Verify product quantities are restored
        assertEquals(12, product1.getQuantity()); // 10 + 2
        assertEquals(6, product2.getQuantity()); // 5 + 1

        verify(productRepository, times(2)).save(any(Product.class));
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    @Transactional
    void deleteOrder_Cancelled_ShouldNotRestoreQuantities() {
        // Arrange
        order.setStatus(Order.Status.CANCELLED);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(order);

        // Act
        orderService.deleteOrder(1L);

        // Assert
        // Verify product quantities are not changed
        assertEquals(10, product1.getQuantity());
        assertEquals(5, product2.getQuantity());

        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, times(1)).delete(order);
    }
}