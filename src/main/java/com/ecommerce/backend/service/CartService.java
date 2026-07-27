package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.CartItemRequest;
import com.ecommerce.backend.entity.CartItem;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<CartItem> getCart(String userEmail) {
        User user = getUser(userEmail);
        return cartItemRepository.findByUserId(user.getId());
    }

    public CartItem addToCart(String userEmail, CartItemRequest request) {
        User user = getUser(userEmail);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock for product: " + product.getName());
        }

        CartItem existing = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId()).orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            return cartItemRepository.save(existing);
        }

        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(request.getQuantity());
        return cartItemRepository.save(item);
    }

    public CartItem updateQuantity(String userEmail, Long cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        validateOwnership(item, userEmail);

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public void removeFromCart(String userEmail, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        validateOwnership(item, userEmail);
        cartItemRepository.delete(item);
    }

    public void clearCart(String userEmail) {
        User user = getUser(userEmail);
        cartItemRepository.deleteByUserId(user.getId());
    }

    private void validateOwnership(CartItem item, String userEmail) {
        if (!item.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("You are not authorized to modify this cart item");
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
