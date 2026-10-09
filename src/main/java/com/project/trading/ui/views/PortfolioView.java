package com.project.trading.ui.views;

import com.project.trading.exception.DataAccessException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Portfolio;
import com.project.trading.service.PortfolioService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.text.NumberFormat;
import java.util.Locale;

@Route(value = "portfolio", layout = MainLayout.class)
@PageTitle("Portfolio | SimTrade")
public class PortfolioView extends VerticalLayout {

    private final PortfolioService portfolioService;
    private final SecurityService securityService;
    private final Grid<PortfolioService.HoldingRow> grid;
    private final Span errorMessage;
    private final NumberFormat currencyFormat;
    private final NumberFormat percentFormat;

    private Span cashLabel;
    private Span investedLabel;
    private Span currentValueLabel;
    private Span netWorthLabel;

    public PortfolioView() {
        this.portfolioService = new PortfolioService();
        this.securityService = new SecurityService();
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.percentFormat = NumberFormat.getPercentInstance(Locale.US);
        this.percentFormat.setMinimumFractionDigits(2);

        setSizeFull();

        errorMessage = new Span();
        errorMessage.getStyle().set("color", "var(--lumo-error-text-color)");
        errorMessage.setVisible(false);

        grid = new Grid<>(PortfolioService.HoldingRow.class, false);
        grid.setSizeFull();
        grid.addColumn(row -> row.symbol).setHeader("Symbol").setSortable(true);
        grid.addColumn(row -> row.companyName).setHeader("Company").setSortable(true);
        grid.addColumn(row -> row.quantity).setHeader("Quantity").setSortable(true);
        grid.addColumn(row -> currencyFormat.format(row.avgBuyPrice)).setHeader("Avg Price").setSortable(true);
        grid.addColumn(row -> currencyFormat.format(row.currentPrice)).setHeader("Current Price").setSortable(true);
        grid.addColumn(row -> currencyFormat.format(row.currentValue)).setHeader("Total Value").setSortable(true);
        
        grid.addComponentColumn(row -> {
            Span pnlSpan = new Span(currencyFormat.format(row.unrealizedPnl) + " (" + percentFormat.format(row.unrealizedPct / 100.0) + ")");
            if (row.isPositive()) {
                pnlSpan.getStyle().set("color", "green");
            } else {
                pnlSpan.getStyle().set("color", "red");
            }
            return pnlSpan;
        }).setHeader("Unrealized P&L").setSortable(true);

        add(errorMessage, createSummaryHeader(), grid);
        updateData();
    }

    private HorizontalLayout createSummaryHeader() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        cashLabel = new Span();
        investedLabel = new Span();
        currentValueLabel = new Span();
        netWorthLabel = new Span();

        VerticalLayout cashLayout = new VerticalLayout(new H2("Cash"), cashLabel);
        VerticalLayout investedLayout = new VerticalLayout(new H2("Invested"), investedLabel);
        VerticalLayout currentLayout = new VerticalLayout(new H2("Current Value"), currentValueLabel);
        VerticalLayout netLayout = new VerticalLayout(new H2("Net Worth"), netWorthLabel);

        layout.add(cashLayout, investedLayout, currentLayout, netLayout);
        return layout;
    }

    private void updateData() {
        try {
            AuthUser user = securityService.getAuthenticatedUser();
            if (user == null) {
                return; // Guarded by AuthInitListener anyway
            }

            Portfolio portfolio = portfolioService.getPortfolioForUser(user.getId());
            PortfolioService.PortfolioSummary summary = portfolioService.buildSummary(portfolio);

            cashLabel.setText(currencyFormat.format(summary.cash));
            investedLabel.setText(currencyFormat.format(summary.totalInvested));
            currentValueLabel.setText(currencyFormat.format(summary.totalCurrentValue));
            netWorthLabel.setText(currencyFormat.format(summary.netWorth));

            if (summary.rows.isEmpty()) {
                errorMessage.setText("Your portfolio is empty. Go to the Market to start trading.");
                errorMessage.setVisible(true);
                grid.setItems();
            } else {
                errorMessage.setVisible(false);
                grid.setItems(summary.rows);
            }
        } catch (DataAccessException e) {
            errorMessage.setText("Error loading portfolio data. Please try again later.");
            errorMessage.setVisible(true);
            grid.setItems();
        }
    }
}
