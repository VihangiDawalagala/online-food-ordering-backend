package com.example.foodorder.controller;

import com.example.foodorder.entity.Cart;
import com.example.foodorder.entity.CartItem;
import com.example.foodorder.entity.Role;
import com.example.foodorder.entity.User;
import com.example.foodorder.exception.ResourceNotFoundException;
import com.example.foodorder.repository.CartItemRepository;
import com.example.foodorder.repository.UserRepository;
import com.example.foodorder.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(
            @PathVariable Long userId,
            Authentication authentication
    ) {

        verifyUserAccess(userId, authentication);

        return ResponseEntity.ok(
                cartService.getCartByUser(userId)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @RequestBody Map<String, Object> request,
            Authentication authentication
    ) {

        Long userId = Long.valueOf(
                request.get("userId").toString());

        verifyUserAccess(userId, authentication);

        Long foodId = Long.valueOf(
                request.get("foodId").toString());

        Integer quantity = Integer.valueOf(
                request.get("quantity").toString());

        return ResponseEntity.ok(
                cartService.addFoodToCart(
                        userId,
                        foodId,
                        quantity
                )
        );
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeCartItem(
            @PathVariable Long cartItemId,
            Authentication authentication
    ) {

        verifyCartItemAccess(cartItemId, authentication);

        cartService.removeCartItem(cartItemId);

        return ResponseEntity.ok("Removed");
    }

    private void verifyUserAccess(
            Long userId,
            Authentication authentication
    ) {

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authenticated user not found"));

        if (currentUser.getRole() != Role.ADMIN &&
                !currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You can only access your own cart");
        }
    }

    private void verifyCartItemAccess(
            Long cartItemId,
            Authentication authentication
    ) {

        User currentUser = getCurrentUser(authentication);

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));

        Long ownerId = cartItem.getCart().getUser().getId();
        if (!currentUser.getId().equals(ownerId)) {
            throw new AccessDeniedException("You can only update your own cart");
        }
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authenticated user not found"));
    }
}
