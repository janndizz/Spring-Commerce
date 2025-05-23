package vn.edu.tdtu.javatech.springcommerce.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.tdtu.javatech.springcommerce.model.Product;
import vn.edu.tdtu.javatech.springcommerce.repository.ProductRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setTitle("Product 1");
        product1.setPrice(100.0);

        product2 = new Product();
        product2.setId(2L);
        product2.setTitle("Product 2");
        product2.setPrice(200.0);
    }

    @Test
    void getFeaturedProducts_ShouldReturnTop5ProductsOrderByIdDesc() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productRepository.findTop5ByOrderByIdDesc()).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.getFeaturedProducts();

        // Assert
        assertEquals(2, actualProducts.size());
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository, times(1)).findTop5ByOrderByIdDesc();
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.getAllProducts();

        // Assert
        assertEquals(2, actualProducts.size());
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        // Act
        Product actualProduct = productService.getProductById(productId);

        // Assert
        assertNotNull(actualProduct);
        assertEquals(product1.getId(), actualProduct.getId());
        assertEquals(product1.getTitle(), actualProduct.getTitle());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void getProductById_WhenProductNotExists_ShouldThrowException() {
        // Arrange
        Long productId = 99L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.getProductById(productId);
        });
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void getLastedProducts_ShouldReturnTop5ProductsOrderByIdAsc() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productRepository.findTop5ByOrderByIdAsc()).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.getLastedProducts();

        // Assert
        assertEquals(2, actualProducts.size());
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository, times(1)).findTop5ByOrderByIdAsc();
    }

    @Test
    void saveProduct_ShouldReturnSavedProduct() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setTitle("New Product");
        when(productRepository.save(newProduct)).thenReturn(product1);

        // Act
        Product savedProduct = productService.saveProduct(newProduct);

        // Assert
        assertNotNull(savedProduct);
        assertEquals(product1.getId(), savedProduct.getId());
        verify(productRepository, times(1)).save(newProduct);
    }

    @Test
    void updateProduct_WhenProductExists_ShouldReturnUpdatedProduct() {
        // Arrange
        Long productId = 1L;
        Product updatedProduct = new Product();
        updatedProduct.setTitle("Updated Product");
        updatedProduct.setPrice(150.0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        when(productRepository.save(product1)).thenReturn(product1);

        // Act
        Product result = productService.updateProduct(productId, updatedProduct);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Product", result.getTitle());
        assertEquals(150.0, result.getPrice());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void updateProduct_WhenProductNotExists_ShouldThrowException() {
        // Arrange
        Long productId = 99L;
        Product updatedProduct = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(productId, updatedProduct);
        });
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_ShouldCallRepositoryDelete() {
        // Arrange
        Long productId = 1L;
        doNothing().when(productRepository).deleteById(productId);

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void filterProducts_ShouldReturnFilteredProducts() {
        // Arrange
        String genre = "Rock";
        String artist = "Artist1";
        String brand = "Brand1";
        String color = "Red";
        Double minPrice = 100.0;
        Double maxPrice = 200.0;

        List<Product> expectedProducts = Arrays.asList(product1);
        when(productRepository.findWithFilters(genre, artist, brand, color, minPrice, maxPrice))
                .thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.filterProducts(
                genre, artist, brand, color, minPrice, maxPrice);

        // Assert
        assertEquals(1, actualProducts.size());
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository, times(1))
                .findWithFilters(genre, artist, brand, color, minPrice, maxPrice);
    }

    @Test
    void getAllGenres_ShouldReturnAllGenres() {
        // Arrange
        List<String> expectedGenres = Arrays.asList("Rock", "Pop");
        when(productRepository.findAllGenres()).thenReturn(expectedGenres);

        // Act
        List<String> actualGenres = productService.getAllGenres();

        // Assert
        assertEquals(2, actualGenres.size());
        assertEquals(expectedGenres, actualGenres);
        verify(productRepository, times(1)).findAllGenres();
    }

    // Tương tự viết test cho getAllBrands() và getAllColors()
    @Test
    void getAllBrands_ShouldReturnAllBrands() {
        // Arrange
        List<String> expectedBrands = Arrays.asList("Brand1", "Brand2");
        when(productRepository.findAllBrands()).thenReturn(expectedBrands);

        // Act
        List<String> actualBrands = productService.getAllBrands();

        // Assert
        assertEquals(2, actualBrands.size());
        assertEquals(expectedBrands, actualBrands);
        verify(productRepository, times(1)).findAllBrands();
    }

    @Test
    void getAllColors_ShouldReturnAllColors() {
        // Arrange
        List<String> expectedColors = Arrays.asList("Red", "Blue");
        when(productRepository.findAllColors()).thenReturn(expectedColors);

        // Act
        List<String> actualColors = productService.getAllColors();

        // Assert
        assertEquals(2, actualColors.size());
        assertEquals(expectedColors, actualColors);
        verify(productRepository, times(1)).findAllColors();
    }
}