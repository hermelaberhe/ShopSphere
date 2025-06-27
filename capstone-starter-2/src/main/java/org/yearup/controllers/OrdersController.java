package org.yearup.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.*;
import org.yearup.models.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController
{
    private final OrderDao orderDao;
    private final ShoppingCartDao shoppingCartDao;
    private final ProductDao productDao;
    private final UserDao userDao;
    private final ProfileDao profileDao;

    public OrdersController(OrderDao orderDao, ShoppingCartDao shoppingCartDao, ProductDao productDao, UserDao userDao, ProfileDao profileDao)
    {
        this.orderDao = orderDao;
        this.shoppingCartDao = shoppingCartDao;
        this.productDao = productDao;
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    @PostMapping
    public ResponseEntity<?> checkout(Principal principal)
    {
        try
        {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            if (user == null)
            {
                return ResponseEntity.status(401).body("User not found.");
            }
            int userId = user.getId();

            ShoppingCart cart = shoppingCartDao.getByUserId(userId);
            if (cart == null || cart.getItems().isEmpty())
            {
                return ResponseEntity.badRequest().body("Shopping cart is empty.");
            }

            Profile profile = profileDao.getByUserId(userId);
            if (profile == null)
            {
                return ResponseEntity.badRequest().body("User profile/shipping info missing.");
            }

            // Calculate shipping amount (you can customize this)
            BigDecimal shippingAmount = BigDecimal.ZERO;

            // Create order
            int orderId = orderDao.createOrder(
                    userId,
                    new Date(),
                    profile.getAddress(),
                    profile.getCity(),
                    profile.getState(),
                    profile.getZip(),
                    shippingAmount
            );

            // Add order line items
            for ( ShoppingCartItem item : cart.getItems().values())
            {
                Product product = productDao.getById(item.getProductId());
                if (product == null)
                {
                    return ResponseEntity.badRequest().body("Product not found: " + item.getProductId());
                }

                orderDao.addOrderLineItem(
                        orderId,
                        item.getProductId(),
                        product.getPrice(),
                        item.getQuantity(),
                        BigDecimal.ZERO // discount, adjust if you have any logic
                );
            }

            // Clear shopping cart
            shoppingCartDao.clearCart(userId);

            return ResponseEntity.ok("Order placed successfully with Order ID: " + orderId);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(500).body("Checkout failed: " + e.getMessage());
        }
    }
}