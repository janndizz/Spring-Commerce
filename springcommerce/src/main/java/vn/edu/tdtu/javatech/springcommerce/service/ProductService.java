package vn.edu.tdtu.javatech.springcommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.javatech.springcommerce.model.Product;
import vn.edu.tdtu.javatech.springcommerce.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findTop5ByOrderByIdDesc();
    }
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public Product getProductById(Long id) {
        return productRepository.findById(id).get();
    }
    public List<Product> getLastedProducts() {
        return productRepository.findTop5ByOrderByIdAsc();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = getProductById(id);
        existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setArtist(updatedProduct.getArtist());
        existingProduct.setGenre(updatedProduct.getGenre());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setColor(updatedProduct.getColor());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        existingProduct.setQuantity(updatedProduct.getQuantity());

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> filterProducts(String genre, String artist, String brand,
                                        String color, Double minPrice, Double maxPrice) {
        return productRepository.findWithFilters(genre, artist, brand, color, minPrice, maxPrice);
    }

    public List<String> getAllGenres() {
        return productRepository.findAllGenres();
    }

    public List<String> getAllBrands() {
        return productRepository.findAllBrands();
    }

    public List<String> getAllColors() {
        return productRepository.findAllColors();
    }


}
