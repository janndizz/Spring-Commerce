package vn.edu.tdtu.javatech.springcommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.tdtu.javatech.springcommerce.model.Order;
import vn.edu.tdtu.javatech.springcommerce.model.User;
import vn.edu.tdtu.javatech.springcommerce.model.Order.Status;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Lấy đơn hàng kèm danh sách sản phẩm
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    // Lấy tất cả đơn hàng kèm danh sách sản phẩm
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items")
    List<Order> findAllWithItems();

    // Lấy đơn hàng theo user kèm danh sách sản phẩm
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.user = :user")
    List<Order> findByUserWithItems(@Param("user") User user);

    // Lấy đơn hàng theo trạng thái kèm danh sách sản phẩm
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.status = :status")
    List<Order> findByStatusWithItems(@Param("status") Status status);

    // Các phương thức có sẵn
    List<Order> findByUser(User user);
    List<Order> findByStatus(Status status);
}