// OrderLineItem.java
package org.yearup.data;

import org.yearup.models.OrderLineItem;

import java.math.BigDecimal;

public class OrderLineItemDao
{
    private int orderLineItemId;
    private int orderId;
    private int productId;
    private BigDecimal salesPrice;
    private int quantity;
    private BigDecimal discount;

    // Getters and setters
    public int getOrderLineItemId() { return orderLineItemId; }
    public void setOrderLineItemId(int orderLineItemId) { this.orderLineItemId = orderLineItemId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public BigDecimal getSalesPrice() { return salesPrice; }
    public void setSalesPrice(BigDecimal salesPrice) { this.salesPrice = salesPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public void create(OrderLineItem lineItem) {
    }

    public void deleteByOrderId(int orderId) {
    }
}

