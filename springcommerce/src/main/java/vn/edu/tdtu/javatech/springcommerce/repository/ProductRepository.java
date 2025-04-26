package vn.edu.tdtu.javatech.springcommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.tdtu.javatech.springcommerce.model.Product;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findTop5ByOrderByIdDesc(); // 5 sản phẩm mới nhất
}



