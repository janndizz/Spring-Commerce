package vn.edu.tdtu.javatech.springcommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.javatech.springcommerce.model.Product;
import vn.edu.tdtu.javatech.springcommerce.repository.ProductRepository;
import java.util.Optional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getFeaturedProducts() {
        return productRepository.findTop5ByOrderByIdDesc();
    }
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public Product getProductById(Long id) {
        return productRepository.findById(id).get();
    }

}
