package com.example.foodorder;

import com.example.foodorder.controller.FoodController;
import com.example.foodorder.dto.AuthResponse;
import com.example.foodorder.dto.SignInRequest;
import com.example.foodorder.dto.SignUpRequest;
import com.example.foodorder.entity.Cart;
import com.example.foodorder.entity.CartItem;
import com.example.foodorder.entity.FoodItem;
import com.example.foodorder.entity.FoodStatus;
import com.example.foodorder.entity.Order;
import com.example.foodorder.entity.OrderStatus;
import com.example.foodorder.entity.Payment;
import com.example.foodorder.entity.PaymentStatus;
import com.example.foodorder.entity.Role;
import com.example.foodorder.entity.User;
import com.example.foodorder.repository.CartItemRepository;
import com.example.foodorder.repository.CartRepository;
import com.example.foodorder.repository.FoodRepository;
import com.example.foodorder.repository.OrderRepository;
import com.example.foodorder.repository.PaymentRepository;
import com.example.foodorder.repository.UserRepository;
import com.example.foodorder.security.JwtUtil;
import com.example.foodorder.service.AuthService;
import com.example.foodorder.service.CartService;
import com.example.foodorder.service.OrderService;
import com.example.foodorder.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodOrderFeatureTests {

    @Test
    void signUpCreatesCustomerAndReturnsJwt() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(userRepository.existsByEmail("customer@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userDetailsService.loadUserByUsername("customer@example.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        AuthService authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtUtil,
                authenticationManager,
                userDetailsService
        );

        SignUpRequest request = new SignUpRequest();
        request.setName("Customer One");
        request.setEmail("customer@example.com");
        request.setPassword("password123");

        AuthResponse response = authService.signUp(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("CUSTOMER", response.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signInAuthenticatesAndReturnsJwt() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        UserDetails userDetails = mock(UserDetails.class);

        User user = User.builder()
                .id(1L)
                .name("Customer One")
                .email("customer@example.com")
                .password("encoded-password")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("customer@example.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        AuthService authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtUtil,
                authenticationManager,
                userDetailsService
        );

        SignInRequest request = new SignInRequest();
        request.setEmail("customer@example.com");
        request.setPassword("password123");

        AuthResponse response = authService.signIn(request);

        assertEquals(1L, response.getId());
        assertEquals("jwt-token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void foodCreationEndpointIsAdminOnly() throws NoSuchMethodException {
        Method createFood = FoodController.class.getMethod(
                "createFood",
                FoodItem.class
        );

        PreAuthorize preAuthorize = createFood.getAnnotation(PreAuthorize.class);

        assertNotNull(preAuthorize);
        assertEquals("hasRole('ADMIN')", preAuthorize.value());
    }

    @Test
    void addFoodToCartAddsItemForUser() {
        CartRepository cartRepository = mock(CartRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        FoodRepository foodRepository = mock(FoodRepository.class);
        CartItemRepository cartItemRepository = mock(CartItemRepository.class);

        User user = User.builder().id(1L).email("customer@example.com").build();
        FoodItem foodItem = FoodItem.builder()
                .id(2L)
                .name("Pizza")
                .price(1500.0)
                .status(FoodStatus.AVAILABLE)
                .build();
        Cart cart = Cart.builder()
                .id(3L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(foodRepository.findById(2L)).thenReturn(Optional.of(foodItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(cart)).thenReturn(cart);

        CartService cartService = new CartService(
                cartRepository,
                userRepository,
                foodRepository,
                cartItemRepository
        );

        Cart updatedCart = cartService.addFoodToCart(1L, 2L, 2);

        assertEquals(1, updatedCart.getCartItems().size());
        assertEquals(2, updatedCart.getCartItems().get(0).getQuantity());
        assertSame(foodItem, updatedCart.getCartItems().get(0).getFoodItem());
    }

    @Test
    void placeOrderCreatesPendingPaymentAndClearsCart() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CartRepository cartRepository = mock(CartRepository.class);

        User user = User.builder().id(1L).email("customer@example.com").build();
        FoodItem foodItem = FoodItem.builder()
                .id(2L)
                .name("Burger")
                .price(1000.0)
                .build();
        Cart cart = Cart.builder()
                .id(3L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();
        cart.getCartItems().add(CartItem.builder()
                .cart(cart)
                .foodItem(foodItem)
                .quantity(3)
                .build());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderService orderService = new OrderService(
                orderRepository,
                userRepository,
                cartRepository
        );

        Order order = orderService.placeOrder(1L);

        assertEquals(OrderStatus.PLACED, order.getStatus());
        assertEquals(1, order.getOrderItems().size());
        assertEquals(PaymentStatus.PENDING, order.getPayment().getStatus());
        assertEquals(3000.0, order.getPayment().getAmount());
        assertTrue(cart.getCartItems().isEmpty());
    }

    @Test
    void processPaymentUpdatesExistingPendingPayment() {
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);

        Payment payment = Payment.builder()
                .id(10L)
                .amount(3000.0)
                .status(PaymentStatus.PENDING)
                .build();
        Order order = Order.builder()
                .id(5L)
                .payment(payment)
                .build();
        payment.setOrder(order);

        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentService paymentService = new PaymentService(
                paymentRepository,
                orderRepository
        );

        Payment completedPayment = paymentService.processPayment(5L, 3000.0);

        assertSame(payment, completedPayment);
        assertEquals(PaymentStatus.COMPLETED, completedPayment.getStatus());
        assertEquals(3000.0, completedPayment.getAmount());
    }
}
