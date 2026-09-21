package com.project.trading.engine;

import com.project.trading.model.LimitOrder;
import com.project.trading.model.Order;

public class OrderBookTest {

    public static void main(String[] args) {

        OrderBook book = new OrderBook("TCS");

        book.addOrder(new LimitOrder(1, 101, "TCS", Order.Side.BUY, 10, Order.Status.PENDING, 3500.0));
        book.addOrder(new LimitOrder(2, 102, "TCS", Order.Side.BUY, 5, Order.Status.PENDING, 3550.0));
        book.addOrder(new LimitOrder(3, 103, "TCS", Order.Side.BUY, 8, Order.Status.PENDING, 3550.0));

        book.printBook();

        System.out.println("Best buy order id: " + book.peekBestBuy().getId());
    }
}