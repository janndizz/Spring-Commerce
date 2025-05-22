package vn.edu.tdtu.javatech.springcommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.tdtu.javatech.springcommerce.model.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
}



