package com.project.trading.ui.admin;

import com.project.trading.listener.AppContextListener;
import com.project.trading.model.Stock;
import com.project.trading.service.StockService;
import com.project.trading.simulator.PriceSimulator;
import com.project.trading.util.FaultInjector;
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
@PageTitle("Simulator & Tools | Admin")
public class AdminToolsView extends VerticalLayout {

    private final StockService stockService = new StockService();
    private PriceSimulator priceSimulator;

    private Span simulatorStatus;
    private Button toggleSimulatorBtn;

    private Span faultStatus;
    private Button toggleFaultBtn;

    public AdminToolsView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Simulator & Testing Tools");
        title.getStyle().set("margin-top", "0").set("color", "var(--st-text-main)");

        // Fetch Simulator instance from ServletContext
        Object attr = VaadinServlet.getCurrent().getServletContext().getAttribute(AppContextListener.SIMULATOR_ATTR);
        if (attr instanceof PriceSimulator) {
            this.priceSimulator = (PriceSimulator) attr;
        }

        add(title);
        
        HorizontalLayout topSection = new HorizontalLayout();
        topSection.setWidthFull();
        topSection.setSpacing(true);
        
        topSection.add(createSimulatorSection(), createFaultInjectorSection());
        add(topSection);
        
        add(createManualPriceSection());
    }

    private com.vaadin.flow.component.Component createSimulatorSection() {
        Div section = new Div();
        section.setWidth("100%");
        section.getStyle()
                .set("background", "#ffffff")
                .set("border", "1px solid var(--st-border-color)")
                .set("border-radius", "8px")
                .set("padding", "20px");

        H2 header = new H2("Background Price Simulator");
        header.getStyle().set("font-size", "1.2rem").set("margin-top", "0");
        
        Paragraph desc = new Paragraph("A background thread that randomly fluctuates active stock prices every 5 seconds.");
        desc.getStyle().set("color", "var(--st-text-muted)").set("margin-top", "0");

        simulatorStatus = new Span();
        simulatorStatus.getStyle().set("padding", "4px 8px").set("border-radius", "4px").set("font-weight", "bold");
        
        toggleSimulatorBtn = new Button();
        toggleSimulatorBtn.addClickListener(e -> {
            if (priceSimulator != null) {
                if (priceSimulator.isRunning()) {
                    priceSimulator.stop();
                    showSuccess("Simulator stopped.");
                } else {
                    priceSimulator.start(5);
                    showSuccess("Simulator started.");
                }
                updateSimulatorUI();
            } else {
                showError("Simulator instance not found.");
            }
        });

        updateSimulatorUI();

        HorizontalLayout controls = new HorizontalLayout(simulatorStatus, toggleSimulatorBtn);
        controls.setAlignItems(Alignment.CENTER);
        controls.getStyle().set("margin-top", "16px");

        section.add(header, desc, controls);
        return section;
    }

    private void updateSimulatorUI() {
        if (priceSimulator != null && priceSimulator.isRunning()) {
            simulatorStatus.setText("RUNNING");
            simulatorStatus.getStyle().set("background", "rgba(16, 185, 129, 0.1)").set("color", "var(--st-success-color)");
            toggleSimulatorBtn.setText("Stop Simulator");
            toggleSimulatorBtn.getStyle().set("background", "var(--st-danger-color)").set("color", "white");
        } else {
            simulatorStatus.setText("STOPPED");
            simulatorStatus.getStyle().set("background", "rgba(239, 68, 68, 0.1)").set("color", "var(--st-danger-color)");
            toggleSimulatorBtn.setText("Start Simulator");
            toggleSimulatorBtn.getStyle().set("background", "var(--st-success-color)").set("color", "white");
        }
    }

    private com.vaadin.flow.component.Component createFaultInjectorSection() {
        Div section = new Div();
        section.setWidth("100%");
        section.getStyle()
                .set("background", "#fff0f0")
                .set("border", "1px solid var(--st-danger-color)")
                .set("border-radius", "8px")
                .set("padding", "20px");

        H2 header = new H2("DEMO TOOL: Fault Injection");
        header.getStyle().set("font-size", "1.2rem").set("margin-top", "0").set("color", "var(--st-danger-color)");
        
        Paragraph desc = new Paragraph("Forces a RuntimeException mid-trade to demonstrate database transaction rollback (cash and holdings will not be saved).");
        desc.getStyle().set("color", "var(--st-text-muted)").set("margin-top", "0");

        faultStatus = new Span();
        faultStatus.getStyle().set("padding", "4px 8px").set("border-radius", "4px").set("font-weight", "bold");

        toggleFaultBtn = new Button();
        toggleFaultBtn.addClickListener(e -> {
            FaultInjector.failMidTrade = !FaultInjector.failMidTrade;
            if (FaultInjector.failMidTrade) {
                showError("Fault Injector is now ACTIVE! Trades will fail.");
            } else {
                showSuccess("Fault Injector deactivated.");
            }
            updateFaultUI();
        });

        updateFaultUI();

        HorizontalLayout controls = new HorizontalLayout(faultStatus, toggleFaultBtn);
        controls.setAlignItems(Alignment.CENTER);
        controls.getStyle().set("margin-top", "16px");

        section.add(header, desc, controls);
        return section;
    }

    private void updateFaultUI() {
        if (FaultInjector.failMidTrade) {
            faultStatus.setText("ACTIVE");
            faultStatus.getStyle().set("background", "var(--st-danger-color)").set("color", "white");
            toggleFaultBtn.setText("Deactivate Faults");
            toggleFaultBtn.getStyle().set("background", "white").set("color", "var(--st-danger-color)").set("border", "1px solid var(--st-danger-color)");
        } else {
            faultStatus.setText("OFF");
            faultStatus.getStyle().set("background", "rgba(107, 114, 128, 0.1)").set("color", "var(--st-text-muted)");
            toggleFaultBtn.setText("Activate Faults");
            toggleFaultBtn.getStyle().set("background", "var(--st-danger-color)").set("color", "white");
        }
    }

    private com.vaadin.flow.component.Component createManualPriceSection() {
        Div section = new Div();
        section.setWidth("100%");
        section.getStyle()
                .set("background", "#ffffff")
                .set("border", "1px solid var(--st-border-color)")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("margin-top", "24px");

        H2 header = new H2("Manual Price Update");
        header.getStyle().set("font-size", "1.2rem").set("margin-top", "0");
        
        ComboBox<Stock> stockSelect = new ComboBox<>("Select Stock");
        stockSelect.setItems(stockService.listAll());
        stockSelect.setItemLabelGenerator(s -> s.getSymbol() + " - " + s.getCompanyName());
        stockSelect.setWidth("300px");
        
        BigDecimalField priceField = new BigDecimalField("New Price");
        priceField.setWidth("200px");
        
        stockSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                priceField.setValue(e.getValue().getPrice());
            } else {
                priceField.clear();
            }
        });
        
        Button updateBtn = new Button("Update Price", e -> {
            Stock selected = stockSelect.getValue();
            if (selected == null) {
                showError("Please select a stock.");
                return;
            }
            if (priceField.getValue() == null) {
                showError("Please enter a valid price.");
                return;
            }
            try {
                stockService.updatePrice(selected.getId(), priceField.getValue());
                showSuccess("Price updated successfully for " + selected.getSymbol());
                
                // Refresh items to show new price next time
                Stock updated = stockService.getById(selected.getId());
                stockSelect.setItems(stockService.listAll());
                stockSelect.setValue(updated);
            } catch (Exception ex) {
                showError("Failed to update price: " + ex.getMessage());
            }
        });
        updateBtn.getStyle().set("background", "var(--st-primary-color)").set("color", "white");
        
        HorizontalLayout form = new HorizontalLayout(stockSelect, priceField, updateBtn);
        form.setAlignItems(Alignment.BASELINE);
        
        section.add(header, form);
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
