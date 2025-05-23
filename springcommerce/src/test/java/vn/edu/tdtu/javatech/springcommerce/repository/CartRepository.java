package vn.edu.tdtu.javatech.springcommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.tdtu.javatech.springcommerce.model.Cart;
import java.util.List;
import java.util.Optional;
import vn.edu.tdtu.javatech.springcommerce.model.*;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}



