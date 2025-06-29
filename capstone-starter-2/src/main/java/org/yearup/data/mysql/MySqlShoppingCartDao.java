package org.yearup.data.mysql;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Repository
public class MySqlShoppingCartDao implements ShoppingCartDao
{
    private final JdbcTemplate jdbcTemplate;
    private final ProductDao productDao;

    public MySqlShoppingCartDao(JdbcTemplate jdbcTemplate, ProductDao productDao)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getByUserId(int userId)
    {
        String sql = "SELECT product_id, quantity FROM shopping_cart WHERE user_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, userId);

        Map<Integer, ShoppingCartItem> items = new HashMap<>();

        while (rs.next())
        {
            int productId = rs.getInt("product_id");
            int quantity = rs.getInt("quantity");

            Product product = productDao.getById(productId);
            if (product == null) continue;

            ShoppingCartItem item = new ShoppingCartItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setDiscountPercent(BigDecimal.ZERO);

            items.put(productId, item);
        }

        ShoppingCart cart = new ShoppingCart();
        cart.setItems(items);

        return cart;
    }

    @Override
    public ShoppingCartItem getItem(int userId, int productId)
    {
        String sql = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, userId, productId);

        if (rs.next())
        {
            int quantity = rs.getInt("quantity");
            Product product = productDao.getById(productId);
            if (product == null) return null;

            ShoppingCartItem item = new ShoppingCartItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setDiscountPercent(BigDecimal.ZERO);

            return item;
        }

        return null;
    }

    @Override
    public void addItem(int userId, int productId, int quantity)
    {
        String sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, productId, quantity);
    }

    @Override
    public void updateQuantity(int userId, int productId, int quantity)
    {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        jdbcTemplate.update(sql, quantity, userId, productId);
    }

    @Override
    public void clearCart(int userId)
    {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public void clear(int id) {

    }
}
