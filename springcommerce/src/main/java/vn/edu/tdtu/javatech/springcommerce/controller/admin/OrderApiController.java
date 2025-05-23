package vn.edu.tdtu.javatech.springcommerce.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.javatech.springcommerce.model.Order;
import vn.edu.tdtu.javatech.springcommerce.model.Order.Status;
import vn.edu.tdtu.javatech.springcommerce.service.OrderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/orders")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        return orderService.findOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Status newStatus = Status.valueOf(request.get("status"));
            Order updatedOrder = orderService.updateOrderStatus(id, newStatus);

            String message = switch (newStatus) {
                case PENDING -> "Đã đặt lại trạng thái chờ xử lý";
                case DELIVERED -> "Đã xác nhận giao hàng thành công";
                case CANCELLED -> "Đã hủy đơn hàng thành công";
            };

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message,
                    "order", updatedOrder
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable Status status) {
        return ResponseEntity.ok(orderService.findOrdersByStatus(status));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã xóa đơn hàng thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
