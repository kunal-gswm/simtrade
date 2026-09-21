package com.project.trading.engine;

import java.util.Comparator;
import java.util.PriorityQueue;

import com.project.trading.model.LimitOrder;
import com.project.trading.model.Order;

public class OrderBook {

    private final String stockSymbol;

    private final PriorityQueue<LimitOrder> buyOrders = new PriorityQueue<>(
        Comparator.comparingDouble(LimitOrder::getLimitPrice).reversed()
                  .thenComparingLong(Order::getSequenceNumber)
    );

    private final PriorityQueue<LimitOrder> sellOrders = new PriorityQueue<>(
        Comparator.comparingDouble(LimitOrder::getLimitPrice)
                  .thenComparingLong(Order::getSequenceNumber)
    );

    public OrderBook(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public void addOrder(LimitOrder order) {
        if (order.getSide() == Order.Side.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
    }

    public LimitOrder peekBestBuy() {
        return buyOrders.peek();
    }

    public LimitOrder peekBestSell() {
        return sellOrders.peek();
    }

    public void printBook() {
        System.out.println("BUY side (best first): " + buyOrders);
        System.out.println("SELL side (best first): " + sellOrders);
    }
}