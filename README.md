# 🏩 ShopSphere - E-Commerce API

Welcome to **ShopSphere** – a simple and powerful backend system for an online store. This project was built using Java and a tool called Spring Boot, which helps organize and run the code smoothly. The backend connects to a MySQL database, which stores all the information about products, users, and shopping activity.

---

## 🏦 What This Project Can Do

### For Customers:

* Browse all products.
* Search for items by:

  * Category (like Electronics or Clothing)
  * Price range (minimum to maximum)
  * Color (like Red or Black)
* View detailed information about any product.
* Add items to a shopping cart.
* Change quantity or remove items from the cart.

### For Admins (Store Owners or Managers):

* Add new products to the store.
* Update existing product information.
* Remove products that are no longer available.

---

## 🛠️ Technologies Used

* **Java & Spring Boot**: This is the main tool that runs the application.
* **MySQL**: A database that stores all the product information.
* **Spring Security & JWT**: Keeps the site secure and ensures only admins can make changes.
* **Postman**: A tool we used to test everything without needing a full website.

---

## 🧪 How We Tested It

We used **Postman** to test every part of the site, like:

* Making sure all products can be viewed.
* Making sure only admins can add or delete products.
* Checking that the shopping cart works – adding, updating, removing items.
* Ensuring the total price in the cart is accurate.

### 🔍 Sample Test Results in Postman

#### 🔑 Logging in as a New User

![Login - new user](../images/login-new-user.png)

#### 🔑 Logging in as Admin

![Login - admin](../images/login-admin.png)

#### 📄 Getting Categories

![Get Categories](../images/get-categories.png)

#### ⛔️ Access Denied for Non-Admin

![Add Category - not admin](../images/add-category-not-admin.png)

#### ✅ Add Category as Admin

![Add Category - as admin](../images/add-category-admin.png)

---

## 🚷 Bug Fix - Search by Price

**The Problem:**
When users tried to search for items between two prices, the system ignored the price and showed everything.

**How We Fixed It:**
We improved the part of the code that does the searching. Now it correctly checks the price range before showing results.

**The Result:**
You can now search for products like: "Show me all items between \$10 and \$50 in the Electronics category."

---

## 📂 How It’s Organized

* `ProductsController.java`: Handles product actions like viewing, adding, or deleting.
* `CartController.java`: Manages the shopping cart.
* `MySqlProductDao.java`: Talks to the database to get or update product info.
* `Product.java`: Describes what a product is (name, price, color, etc).
* `Cart.java`: Keeps track of what’s inside a user’s cart.

---

## 🔐 Who Can Access What

| Action                 | Who Can Do It |
| ---------------------- | ------------- |
| View Products          | Everyone      |
| Search by Filters      | Everyone      |
| Add to Cart            | Everyone      |
| Add New Product        | Admin Only    |
| Edit or Delete Product | Admin Only    |

---

## 📋 How to Use It (Setup)

1. Copy this project from GitHub:
   `git clone https://github.com/hermelaberhe/ShopSphere.git`
2. Make sure your MySQL is running.
3. Fill in your database info in the `application.properties` file.
4. Run the project with Spring Boot.
5. Use Postman or connect it to a website to test it out.

---

## 🎥 Demo Preparation

During the demo, you'll see:

* Products loading and filtering working properly.
* An admin user adding or updating a product.
* A working shopping cart with totals calculated.
* The fixed bug in action (searching by price now works).

