package org.yearup.data;

import org.yearup.models.OrderLineItem;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface OrderDao
{
    int createOrder(int userId, Date date, String address, String city, String state, String zip, BigDecimal shippingAmount);

    void addOrderLineItem(int orderId, int productId, BigDecimal salesPrice, int quantity, BigDecimal discount);

    <OrderLineItem> List<OrderLineItem> getOrderLineItemsByOrderId(int orderId);
}