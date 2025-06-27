package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        String sql = "SELECT category_id, name, description FROM categories";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery())
        {
            while (rs.next())
            {
                categories.add(mapRow(rs));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error fetching categories", e);
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        String sql = "SELECT category_id, name, description FROM categories WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, categoryId);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return mapRow(rs);
                }

                return null;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error fetching category by ID", e);
        }
    }

    @Override
    public Category create(Category category)
    {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys())
            {
                if (keys.next())
                {
                    category.setCategoryId(keys.getInt(1));
                }
            }

            return category;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error creating category", e);
        }
    }

    @Override
    public void update(int categoryId, Category category)
    {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, categoryId);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error updating category", e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, categoryId);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error deleting category", e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        Category category = new Category();
        category.setCategoryId(row.getInt("category_id"));
        category.setName(row.getString("name"));
        category.setDescription(row.getString("description"));
        return category;
    }
}
