package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

@RestController
@RequestMapping("/categories")         // Base URL for all endpoints in this controller
@CrossOrigin                        // Allow Cross-Origin Requests (e.g., from frontend apps)
public class CategoriesController
{
    private final CategoryDao categoryDao;
    private final ProductDao productDao;

    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao)
    {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    @GetMapping
    public List<Category> getAll()
    {
        // find and return all categories
        return categoryDao.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getById(@PathVariable int id)
    {
        // get the category by id
        return categoryDao.getById(id);
    }

    @GetMapping("/{categoryId}/products")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        // get a list of products by categoryId
        return productDao.listByCategoryId(categoryId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")    // Only ADMIN role can insert
    public Category addCategory(@RequestBody Category category)
    {
        // insert the category and return it
        return categoryDao.create(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")    // Only ADMIN role can update
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        // update the category by id
        categoryDao.update(id, category);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")    // Only ADMIN role can delete
    public void deleteCategory(@PathVariable int id)
    {
        // delete the category by id
        categoryDao.delete(id);
    }
}