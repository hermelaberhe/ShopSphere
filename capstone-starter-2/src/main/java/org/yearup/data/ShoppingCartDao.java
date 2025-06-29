package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);

    // add additional method signatures here
    ShoppingCartItem getItem(int userId, int productId);
    void addItem(int userId, int productId, int quantity);
    void updateQuantity(int userId, int productId, int quantity);
    void clearCart(int userId);

    void clear(int id);
}