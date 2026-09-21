package com.project.trading.engine;

import java.time.LocalDateTime;

import com.project.trading.exception.InsufficientFundsException;
import com.project.trading.exception.InvalidOrderException;
import com.project.trading.model.Holding;
import com.project.trading.model.LimitOrder;
import com.project.trading.model.Order;
import com.project.trading.model.Portfolio;
import com.project.trading.model.Trade;

public class MatchingEngine {

    private final OrderBook orderBook;
    private static int tradeIdCounter = 1;

    public MatchingEngine(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    public void processBuyOrder(LimitOrder buyOrder, Portfolio buyerPortfolio,
                                 Portfolio sellerPortfolio, Holding sellerHolding)
            throws InsufficientFundsException, InvalidOrderException {
        
        double maxCost = buyOrder.getQuantity() * buyOrder.getLimitPrice();
        if (buyerPortfolio.getCashBalance() < maxCost) {
            throw new InsufficientFundsException(
                "Buyer cannot afford " + buyOrder.getQuantity() + " shares at max price " + buyOrder.getLimitPrice());
        }

        LimitOrder bestSell = orderBook.peekBestSell();

        // Step 1: is there anyone to trade with at all?
        if (bestSell == null) {
            orderBook.addOrder(buyOrder);   // nobody selling — wait in line
            return;
        }
        

        // Step 2: does the price actually overlap? (Ravi/Priya rule)
        if (buyOrder.getLimitPrice() < bestSell.getLimitPrice()) {
            orderBook.addOrder(buyOrder);   // no overlap — wait in line
            return;
        }

        // Step 3: how much can actually be traded right now?
        int tradeQuantity = Math.min(buyOrder.getQuantity(), bestSell.getQuantity());
        double executionPrice = bestSell.getLimitPrice(); // resting order's price wins

        double totalCost = tradeQuantity * executionPrice;

        // Step 4: can the buyer actually afford it?
        if (buyerPortfolio.getCashBalance() < totalCost) {
            throw new InsufficientFundsException(
                "Buyer cannot afford " + tradeQuantity + " shares at " + executionPrice);
        }

        // Step 5: money and shares actually move
        buyerPortfolio.debit(totalCost);
        sellerPortfolio.credit(totalCost);
        sellerHolding.removeShares(tradeQuantity);

        // Step 6: both orders shrink by what just traded
        buyOrder.reduceQuantity(tradeQuantity);
        bestSell.reduceQuantity(tradeQuantity);

        // Step 7: record what happened
        Trade trade = new Trade(tradeIdCounter++, buyOrder.getId(), bestSell.getId(),
                buyOrder.getStockSymbol(), tradeQuantity, executionPrice, LocalDateTime.now());
        System.out.println("Trade executed: " + trade.getQuantity() + " @ " + trade.getPrice());

        // Step 8: clean up whoever's fully done
        if (bestSell.isFullyFilled()) {
            orderBook.pollBestSell();   // remove them from the line, they're done
            bestSell.setStatus(Order.Status.EXECUTED);
        }

        if (buyOrder.isFullyFilled()) {
            buyOrder.setStatus(Order.Status.EXECUTED);
        } else {
            orderBook.addOrder(buyOrder);   // still wants more — back in line for the rest
        }
    }


    public void processSellOrder(LimitOrder sellOrder, Portfolio sellerPortfolio,
                                  Portfolio buyerPortfolio, Holding sellerHolding)
            throws InvalidOrderException {
        
        double minimumProceeds = sellOrder.getQuantity() * sellOrder.getLimitPrice();
        if (sellerHolding.getQuantity() < sellOrder.getQuantity()) {
            throw new InvalidOrderException(
                "Seller only owns " + sellerHolding.getQuantity() + " shares, cannot sell " + sellOrder.getQuantity());
        }

        LimitOrder bestBuy = orderBook.peekBestBuy();

        // Step 1: is there anyone to trade with at all?
        if (bestBuy == null) {
            orderBook.addOrder(sellOrder);   // nobody buying — wait in line
            return;
        }

        // Step 2: does the price actually overlap? (Ravi/Priya rule)
        if (sellOrder.getLimitPrice() > bestBuy.getLimitPrice()) {
            orderBook.addOrder(sellOrder);   // no overlap — wait in line
            return;
        }

        // Step 3: how much can actually be traded right now?
        int tradeQuantity = Math.min(sellOrder.getQuantity(), bestBuy.getQuantity());
        double executionPrice = bestBuy.getLimitPrice(); // resting order's price wins

        double totalProceeds = tradeQuantity * executionPrice;

        // Step 4: money and shares actually move
        buyerPortfolio.debit(totalProceeds);
        sellerPortfolio.credit(totalProceeds);
        sellerHolding.removeShares(tradeQuantity);

        // Step 5: both orders shrink by what just traded
        sellOrder.reduceQuantity(tradeQuantity);
        bestBuy.reduceQuantity(tradeQuantity);

        // Step 6: record what happened
        Trade trade = new Trade(tradeIdCounter++, bestBuy.getId(), sellOrder.getId(),
                sellOrder.getStockSymbol(), tradeQuantity, executionPrice, LocalDateTime.now());
        System.out.println("Trade executed: " + trade.getQuantity() + " @ " + trade.getPrice());

        // Step 7: clean up whoever's fully done
        if (bestBuy.isFullyFilled()) {
            orderBook.pollBestBuy();   // remove them from the line, they're done
            bestBuy.setStatus(Order.Status.EXECUTED);
        }

        if (sellOrder.isFullyFilled()) {
            sellOrder.setStatus(Order.Status.EXECUTED);
        } else {
            orderBook.addOrder(sellOrder);   // still wants more — back in line for the rest
        }
    }
}