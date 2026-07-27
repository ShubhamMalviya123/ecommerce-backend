package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.CartItemRequest;
import com.ecommerce.backend.entity.CartItem;
import com.ecommerce.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<CartItem> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userDetails.getUsername(), request));
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItem> updateQuantity(@AuthenticationPrincipal UserDetails userDetails,
                                                     @PathVariable Long cartItemId,
                                                     @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(cartService.updateQuantity(userDetails.getUsername(), cartItemId, body.get("quantity")));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable Long cartItemId) {
        cartService.removeFromCart(userDetails.getUsername(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
