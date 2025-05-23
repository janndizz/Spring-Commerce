package vn.edu.tdtu.javatech.springcommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.tdtu.javatech.springcommerce.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findTop5ByOrderByIdDesc();
    List<Product>  findTop5ByOrderByIdAsc();
    @Query("SELECT DISTINCT p.genre FROM Product p")
    List<String> findAllGenres();

    @Query("SELECT DISTINCT p.brand FROM Product p")
    List<String> findAllBrands();

    @Query("SELECT DISTINCT p.color FROM Product p")
    List<String> findAllColors();

    @Query("SELECT p FROM Product p WHERE " +
            "(:genre IS NULL OR p.genre = :genre) AND " +
            "(:artist IS NULL OR p.artist LIKE %:artist%) AND " +
            "(:brand IS NULL OR p.brand = :brand) AND " +
            "(:color IS NULL OR p.color = :color) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> findWithFilters(
            @Param("genre") String genre,
            @Param("artist") String artist,
            @Param("brand") String brand,
            @Param("color") String color,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);
}



