package com.project.trading.ui.admin;

import com.project.trading.listener.AppContextListener;
import com.project.trading.model.Stock;
import com.project.trading.service.StockService;
import com.project.trading.simulator.PriceSimulator;
import com.project.trading.util.FaultInjector;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServlet;

@Route(value = "admin/tools", layout = AdminLayout.class)
@PageTitle("Simulator & Testing Tools | SimTrade Admin")
public class AdminToolsView extends VerticalLayout {

    private final StockService stockService = new StockService();
    private PriceSimulator priceSimulator;

    private Span simulatorStatus;
    private Button toggleSimulatorBtn;

    private Span faultStatus;
    private Button toggleFaultBtn;

    public AdminToolsView() {
        addClassName("st-page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        Div headerContainer = new Div();
        headerContainer.addClassName("st-header-container");

        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");

        H1 title = new H1("Simulation Engine & Diagnostic Controls");
        title.addClassName("st-page-title");
        Paragraph subtitle = new Paragraph("Configure the stochastic price generator, inject transactional faults, and apply manual market overrides.");
        subtitle.addClassName("st-page-subtitle");

        titleGroup.add(title, subtitle);
        headerContainer.add(titleGroup);
        add(headerContainer);

        // Fetch Simulator instance from ServletContext
        Object attr = VaadinServlet.getCurrent().getServletContext().getAttribute(AppContextListener.SIMULATOR_ATTR);
        if (attr instanceof PriceSimulator) {
            this.priceSimulator = (PriceSimulator) attr;
        }

        HorizontalLayout topSection = new HorizontalLayout();
        topSection.setWidthFull();
        topSection.setSpacing(true);
        topSection.getStyle().set("flex-wrap", "wrap");

        topSection.add(createSimulatorSection(), createFaultInjectorSection());
        add(topSection);

        add(createManualPriceSection());
    }

    private Component createSimulatorSection() {
        Div section = new Div();
        section.addClassName("st-table-surface");
        section.getStyle().set("padding", "20px").set("flex", "1").set("min-width", "300px").set("box-sizing", "border-box");

        H2 header = new H2("Stochastic Price Simulator");
        header.addClassName("st-section-title");

        Paragraph desc = new Paragraph("Background daemon worker that introduces random fractional market movement across active equities every 5 seconds.");
        desc.getStyle().set("color", "var(--st-text-secondary)").set("font-size", "13px").set("line-height", "1.5");

        simulatorStatus = new Span();
        simulatorStatus.addClassName("st-badge");

        toggleSimulatorBtn = new Button();
        toggleSimulatorBtn.addClassNames("st-btn");
        toggleSimulatorBtn.getElement().removeAttribute("theme");

        toggleSimulatorBtn.addClickListener(e -> {
            if (priceSimulator != null) {
                if (priceSimulator.isRunning()) {
                    priceSimulator.stop();
                    showSuccess("Price simulator daemon stopped.");
                } else {
                    priceSimulator.start(5);
                    showSuccess("Price simulator daemon started.");
                }
                updateSimulatorUI();
            } else {
                showError("Price simulator instance not found in ServletContext.");
            }
        });

        updateSimulatorUI();

        HorizontalLayout controls = new HorizontalLayout(simulatorStatus, toggleSimulatorBtn);
        controls.setAlignItems(Alignment.CENTER);
        controls.getStyle().set("margin-top", "16px").set("gap", "12px");

        section.add(header, desc, controls);
        return section;
    }

    private void updateSimulatorUI() {
        if (priceSimulator != null && priceSimulator.isRunning()) {
            simulatorStatus.setText("SIMULATOR ACTIVE");
            simulatorStatus.removeClassName("st-badge-sell");
            simulatorStatus.addClassName("st-badge-buy");
            toggleSimulatorBtn.setText("Stop Simulation");
            toggleSimulatorBtn.removeClassName("st-btn-buy");
            toggleSimulatorBtn.addClassName("st-btn-sell");
        } else {
            simulatorStatus.setText("SIMULATOR STOPPED");
            simulatorStatus.removeClassName("st-badge-buy");
            simulatorStatus.addClassName("st-badge-sell");
            toggleSimulatorBtn.setText("Start Simulation");
            toggleSimulatorBtn.removeClassName("st-btn-sell");
            toggleSimulatorBtn.addClassName("st-btn-buy");
        }
    }

    private Component createFaultInjectorSection() {
        Div section = new Div();
        section.addClassName("st-table-surface");
        section.getStyle()
                .set("padding", "20px")
                .set("flex", "1")
                .set("min-width", "300px")
                .set("border-color", "rgba(242, 54, 69, 0.4)")
                .set("box-sizing", "border-box");

        H2 header = new H2("ACID Rollback Diagnostic (Fault Injection)");
        header.addClassName("st-section-title");
        header.getStyle().set("color", "var(--st-red)");

        Paragraph desc = new Paragraph("Forces a synthetic RuntimeException mid-trade to verify transactional rollback integrity across cash balances and portfolio share allocations.");
        desc.getStyle().set("color", "var(--st-text-secondary)").set("font-size", "13px").set("line-height", "1.5");

        faultStatus = new Span();
        faultStatus.addClassName("st-badge");

        toggleFaultBtn = new Button();
        toggleFaultBtn.addClassNames("st-btn");
        toggleFaultBtn.getElement().removeAttribute("theme");

        toggleFaultBtn.addClickListener(e -> {
            FaultInjector.failMidTrade = !FaultInjector.failMidTrade;
            if (FaultInjector.failMidTrade) {
                showError("Fault Injector ACTIVE: Next simulated trade will intentionally fail and roll back.");
            } else {
                showSuccess("Fault Injector deactivated. Trades will settle normally.");
            }
            updateFaultUI();
        });

        updateFaultUI();

        HorizontalLayout controls = new HorizontalLayout(faultStatus, toggleFaultBtn);
        controls.setAlignItems(Alignment.CENTER);
        controls.getStyle().set("margin-top", "16px").set("gap", "12px");

        section.add(header, desc, controls);
        return section;
    }

    private void updateFaultUI() {
        if (FaultInjector.failMidTrade) {
            faultStatus.setText("INJECTOR ENGAGED");
            faultStatus.removeClassName("st-badge-sector");
            faultStatus.addClassName("st-badge-sell");
            toggleFaultBtn.setText("Disengage Faults");
            toggleFaultBtn.removeClassName("st-btn-sell");
            toggleFaultBtn.addClassName("st-btn-secondary");
        } else {
            faultStatus.setText("SYSTEM NORMAL");
            faultStatus.removeClassName("st-badge-sell");
            faultStatus.addClassName("st-badge-sector");
            toggleFaultBtn.setText("Arm Fault Injector");
            toggleFaultBtn.removeClassName("st-btn-secondary");
            toggleFaultBtn.addClassName("st-btn-sell");
        }
    }

    private Component createManualPriceSection() {
        Div section = new Div();
        section.addClassName("st-table-surface");
        section.getStyle().set("padding", "20px").set("margin-top", "16px").set("width", "100%").set("box-sizing", "border-box");

        H2 header = new H2("Manual Instrument Price Override");
        header.addClassName("st-section-title");

        Paragraph desc = new Paragraph("Directly update an equity's reference quote to simulate sudden market shocks or liquidity events.");
        desc.getStyle().set("color", "var(--st-text-secondary)").set("font-size", "13px").set("line-height", "1.5");

        ComboBox<Stock> stockSelect = new ComboBox<>("Select Stock Instrument");
        stockSelect.setItems(stockService.listAll());
        stockSelect.setItemLabelGenerator(s -> s.getSymbol() + " · " + s.getCompanyName());
        stockSelect.setWidth("min(320px, 100%)");

        BigDecimalField priceField = new BigDecimalField("Override Price (₹)");
        priceField.setWidth("min(200px, 100%)");

        stockSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                priceField.setValue(e.getValue().getPrice());
            } else {
                priceField.clear();
            }
        });

        Button updateBtn = new Button("Apply Price Override", e -> {
            Stock selected = stockSelect.getValue();
            if (selected == null) {
                showError("Please select an instrument to update.");
                return;
            }
            if (priceField.getValue() == null) {
                showError("Please enter a valid price.");
                return;
            }
            try {
                stockService.updatePrice(selected.getId(), priceField.getValue());
                showSuccess("Reference price updated successfully for " + selected.getSymbol() + " to ₹" + priceField.getValue());

                Stock updated = stockService.getById(selected.getId());
                stockSelect.setItems(stockService.listAll());
                stockSelect.setValue(updated);
            } catch (Exception ex) {
                showError("Failed to update price: " + ex.getMessage());
            }
        });
        updateBtn.addClassNames("st-btn", "st-btn-primary");
        updateBtn.getElement().removeAttribute("theme");

        HorizontalLayout form = new HorizontalLayout(stockSelect, priceField, updateBtn);
        form.setAlignItems(Alignment.BASELINE);
        form.getStyle().set("margin-top", "12px").set("flex-wrap", "wrap").set("gap", "12px");

        section.add(header, desc, form);
        return section;
    }

    private void showSuccess(String msg) {
        Notification n = Notification.show(msg, 3000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String msg) {
        Notification n = Notification.show(msg, 5000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
