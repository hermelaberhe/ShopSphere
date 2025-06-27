package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.OrderLineItem;

import javax.sql.DataSource;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao
{
    public MySqlOrderDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public int createOrder(int userId, Date date, String address, String city, String state, String zip, BigDecimal shippingAmount)
    {
        String sql = "INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setInt(1, userId);
            ps.setTimestamp(2, new Timestamp(date.getTime()));
            ps.setString(3, address);
            ps.setString(4, city);
            ps.setString(5, state);
            ps.setString(6, zip);
            ps.setBigDecimal(7, shippingAmount);

            ps.executeUpdate();

            try(ResultSet rs = ps.getGeneratedKeys())
            {
                if (rs.next())
                {
                    return rs.getInt(1); // return generated order_id
                }
                else
                {
                    throw new SQLException("Failed to retrieve order ID.");
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error creating order", e);
        }
    }

    @Override
    public void addOrderLineItem(int orderId, int productId, BigDecimal salesPrice, int quantity, BigDecimal discount)
    {
        String sql = "INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount) VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setBigDecimal(3, salesPrice);
            ps.setInt(4, quantity);
            ps.setBigDecimal(5, discount);

            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error adding order line item", e);
        }
    }

    @Override
    public List<OrderLineItem> getOrderLineItemsByOrderId(int orderId)
    {
        List<OrderLineItem> items = new ArrayList<>();
        String sql = "SELECT order_line_item_id, order_id, product_id, sales_price, quantity, discount FROM order_line_items WHERE order_id = ?";

        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, orderId);

            try(ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    OrderLineItem item = new OrderLineItem();
                    item.setOrderLineItemId(rs.getInt("order_line_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setSalesPrice(rs.getBigDecimal("sales_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setDiscount(rs.getBigDecimal("discount"));

                    items.add(item);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error getting order line items", e);
        }

        return items;
    }
}