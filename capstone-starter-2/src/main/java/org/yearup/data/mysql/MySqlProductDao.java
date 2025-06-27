package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.models.Product;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao
{
    public MySqlProductDao(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * @param color
     * @return
     */


    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color)
    {
        List<Product> products = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");

        if (categoryId != null)
            sql.append(" AND category_id = ").append(categoryId);
        if (minPrice != null)
            sql.append(" AND price >= ").append(minPrice);
        if (maxPrice != null)
            sql.append(" AND price <= ").append(maxPrice);
        if (color != null && !color.isEmpty())
            sql.append(" AND color LIKE '%").append(color).append("%'");

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql.toString()))
        {
            while (rs.next())
            {
                products.add(mapRow(rs));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error searching products", e);
        }

        return products;
    }

    @Override
    public List<Product> listByCategoryId(int categoryId)
    {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                products.add(mapRow(rs));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error listing products by category", e);
        }

        return products;
    }

    @Override
    public Product getById(int productId)
    {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                return mapRow(rs);
            }
            return null;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error getting product by ID", e);
        }
    }

    @Override
    public Product create(Product product)
    {
        String sql = "INSERT INTO products (name, price, category_id, description, color, image_url, stock, featured) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getCategoryId());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getColor());
            stmt.setString(6, product.getImageUrl());
            stmt.setInt(7, product.getStock());
            stmt.setBoolean(8, product.isFeatured());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next())
            {
                product.setProductId(keys.getInt(1));
            }

            return product;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error creating product", e);
        }
    }

    @Override
    public void update(int productId, Product product)
    {
        String sql = "UPDATE products SET name = ?, price = ?, category_id = ?, description = ?, " +
                "color = ?, image_url = ?, stock = ?, featured = ? WHERE product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getCategoryId());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getColor());
            stmt.setString(6, product.getImageUrl());
            stmt.setInt(7, product.getStock());
            stmt.setBoolean(8, product.isFeatured());
            stmt.setInt(9, productId);

            int rows = stmt.executeUpdate();
            System.out.println("Rows updated: " + rows);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error updating product", e);
        }
    }

    @Override
    public void delete(int productId)
    {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error deleting product", e);
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException
    {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setDescription(rs.getString("description"));
        p.setColor(rs.getString("color"));
        p.setImageUrl(rs.getString("image_url"));
        p.setStock(rs.getInt("stock"));
        p.setFeatured(rs.getBoolean("featured"));
        return p;
    }
}
