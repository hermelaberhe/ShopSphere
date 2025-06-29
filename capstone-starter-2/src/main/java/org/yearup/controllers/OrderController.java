package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.*;
import org.yearup.models.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@PreAuthorize("isAuthenticated()")
@CrossOrigin
public class OrderController {
    private final ShoppingCartDao shoppingCartDao;
    private final ProfileDao profileDao;
    private final OrderDao orderDao;
    private final OrderLineItemDao orderLineItemDao;
    private final UserDao userDao;

    @Autowired
    public OrderController(
            ShoppingCartDao shoppingCartDao,
            ProfileDao profileDao,
            OrderDao orderDao,
            OrderLineItemDao orderLineItemDao,
            UserDao userDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.profileDao = profileDao;
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
        this.userDao = userDao;
    }

    // ✅ POST /orders - Create a new order from shopping cart
    @PostMapping
    public ResponseEntity<?> checkout(Principal principal) {
        try {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            if (user == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found."));

            ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());
            if (cart == null || cart.getItems().isEmpty())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Shopping cart is empty."));

            Profile profile = profileDao.getByUserId(user.getId());
            if (profile == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "User profile not found."));

            // Create order object
            Order order = new Order();
            order.setUserId(user.getId());
            order.setDate(LocalDateTime.now());
            order.setAddress(profile.getAddress());
            order.setCity(profile.getCity());
            order.setState(profile.getState());
            order.setZip(profile.getZip());
            order.setShippingAmount(BigDecimal.ZERO); // Default

            Order savedOrder = orderDao.create(order);

            // Insert each line item from cart
            for (ShoppingCartItem cartItem : cart.getItems().values()) {
                OrderLineItem lineItem = new OrderLineItem();
                lineItem.setOrderId(savedOrder.getOrderId());
                lineItem.setProductId(cartItem.getProductId());
                lineItem.setQuantity(cartItem.getQuantity());
                lineItem.setSalesPrice(cartItem.getProduct().getPrice());

                BigDecimal subTotal = cartItem.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                BigDecimal discountAmount = subTotal.multiply(cartItem.getDiscountPercent());

                lineItem.setDiscount(discountAmount);

                orderLineItemDao.create(lineItem);
            }

            shoppingCartDao.clear(user.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Order created successfully."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not complete checkout."));
        }
    }

    // ✅ GET /orders - Get all orders for logged-in user
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(Principal principal) {
        String username = principal.getName();
        User user = userDao.getByUserName(username);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Order> orders = orderDao.getOrdersByUserId(user.getId());
        return ResponseEntity.ok(orders);
    }

    // ✅ GET /orders/admin - Admin-only get all orders
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderDao.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ PUT /orders/{orderId}/status - Admin updates order status
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateStatus(@PathVariable int orderId, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");

        try {
            Order order = orderDao.getById(orderId);
            if (order == null) return ResponseEntity.notFound().build();

            order.setStatus(newStatus);
            orderDao.update(order);

            return ResponseEntity.ok("Status updated to " + newStatus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not update status");
        }
    }

    // ✅ DELETE /orders/{orderId} - User deletes their own pending order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable int orderId, Principal principal) {
        try {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            if (user == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found."));

            Order order = orderDao.getById(orderId);
            if (order == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Order not found."));

            if (order.getUserId() != user.getId())
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only delete your own orders."));

            if (!"Pending".equalsIgnoreCase(order.getStatus()))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Only orders with status 'Pending' can be deleted. This order is currently '" + order.getStatus() + "'."));

            orderLineItemDao.deleteByOrderId(orderId);
            orderDao.delete(orderId);

            return ResponseEntity.ok(Map.of("message", "Order deleted successfully."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not delete order."));
        }
    }
}
