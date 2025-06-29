package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.yearup.data.OrderLineItemDao;
import org.yearup.models.OrderLineItem;

@Repository
public class MySqlOrderItemDao extends OrderLineItemDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MySqlOrderItemDao(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(OrderLineItem item)
    {
        final String sql = """
            INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)
            VALUES (?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                item.getOrderId(),
                item.getProductId(),
                item.getSalesPrice(),
                item.getQuantity(),
                item.getDiscount());
    }

    @Override
    public void deleteByOrderId(int orderId)
    {
        final String sql = "DELETE FROM order_line_items WHERE order_id = ?";
        jdbcTemplate.update(sql, orderId);
    }
}
