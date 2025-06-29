package org.yearup.data.mysql;// OrderDaoJdbc.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class MySqlOrderDao implements OrderDao
{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MySqlOrderDao(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Order create(Order order)
    {
        final String sql = "INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, order.getUserId());
            ps.setTimestamp(2, Timestamp.valueOf(order.getDate()));
            ps.setString(3, order.getAddress());
            ps.setString(4, order.getCity());
            ps.setString(5, order.getState());
            ps.setString(6, order.getZip());
            ps.setBigDecimal(7, order.getShippingAmount());
            return ps;
        }, keyHolder);

        int newOrderId = keyHolder.getKey().intValue();
        order.setOrderId(newOrderId);

        return order;
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        String sql = "SELECT order_id, user_id, date, address, city, state, zip, shipping_amount, status " +
                "FROM orders WHERE user_id = ? ORDER BY date DESC";

        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            Order order = new Order();
            order.setOrderId(rs.getInt("order_id"));
            order.setUserId(rs.getInt("user_id"));
            order.setDate(rs.getTimestamp("date").toLocalDateTime());
            order.setAddress(rs.getString("address"));
            order.setCity(rs.getString("city"));
            order.setState(rs.getString("state"));
            order.setZip(rs.getString("zip"));
            order.setShippingAmount(rs.getBigDecimal("shipping_amount"));
            order.setStatus(rs.getString("status"));
            return order;
        });
    }
    @Override
    public List<Order> getAllOrders()
    {
        String sql = "SELECT * FROM orders";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Order order = new Order();
            order.setOrderId(rs.getInt("order_id"));
            order.setUserId(rs.getInt("user_id"));
            order.setDate(rs.getTimestamp("date").toLocalDateTime());
            order.setAddress(rs.getString("address"));
            order.setCity(rs.getString("city"));
            order.setState(rs.getString("state"));
            order.setZip(rs.getString("zip"));
            order.setShippingAmount(rs.getBigDecimal("shipping_amount"));
            order.setStatus(rs.getString("status"));
            return order;
        });
    }
    public void update(Order order) {
        String sql = """
        UPDATE orders SET
            status = ?,
            address = ?, city = ?, state = ?, zip = ?, shipping_amount = ?
        WHERE order_id = ?
        """;
        jdbcTemplate.update(sql,
                order.getStatus(),
                order.getAddress(), order.getCity(), order.getState(), order.getZip(),
                order.getShippingAmount(),
                order.getOrderId());
    }
    public Order getById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{orderId}, (rs, rowNum) -> {
            Order order = new Order();
            order.setOrderId(rs.getInt("order_id"));
            order.setUserId(rs.getInt("user_id"));
            order.setDate(rs.getTimestamp("date").toLocalDateTime());
            order.setAddress(rs.getString("address"));
            order.setCity(rs.getString("city"));
            order.setState(rs.getString("state"));
            order.setZip(rs.getString("zip"));
            order.setShippingAmount(rs.getBigDecimal("shipping_amount"));
            order.setStatus(rs.getString("status")); // Include status
            return order;
        });
    }
    @Override
    public void delete(int orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        jdbcTemplate.update(sql, orderId);
    }
}