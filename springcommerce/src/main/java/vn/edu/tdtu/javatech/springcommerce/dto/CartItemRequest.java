package vn.edu.tdtu.javatech.springcommerce.dto;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long productId;
    private int quantity;
}
