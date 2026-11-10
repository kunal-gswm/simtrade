package com.project.trading.ui.views;

import com.project.trading.exception.AuthenticationException;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("login")
@PageTitle("Terminal Login | SimTrade")
public class LoginView extends Div {

    private static final String SVG_BRAND = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z\" /></svg>";
    private static final String SVG_CHECK = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M4.5 12.75l6 6 9-13.5\" /></svg>";

    public LoginView() {
        addClassName("st-login-page");

        // === Left Brand Panel ===
        Div brandPanel = new Div();
        brandPanel.addClassName("st-login-brand-panel");

        Div brandLogo = new Div();
        brandLogo.addClassName("st-login-brand-logo");
        Component brandIcon = createSvgIcon(SVG_BRAND);
        Span brandName = new Span("SimTrade");
        brandName.addClassName("st-login-brand-name");
        Span tag = new Span("TERMINAL");
        tag.addClassName("st-brand-tag");
        brandLogo.add(brandIcon, brandName, tag);

        Div heading = new Div();
        heading.addClassName("st-login-brand-heading");
        heading.setText("Institutional Paper Trading Architecture.");

        Paragraph desc = new Paragraph("Engineered for serious market simulation with high-precision BigDecimal settlement, HikariCP connection pooling, and real-time execution telemetry.");
        desc.addClassName("st-login-brand-text");

        Div featureList = new Div();
        featureList.addClassName("st-login-feature-list");
        featureList.add(
                createFeatureItem("₹100,000 initial virtual trading capital"),
                createFeatureItem("Thread-safe multi-symbol market simulation"),
                createFeatureItem("Audited trade log with realized/unrealized P&L reconciliation")
        );

        brandPanel.add(brandLogo, heading, desc, featureList);

        // === Right Form Panel ===
        Div formPanel = new Div();
        formPanel.addClassName("st-login-form-panel");

        Div formCard = new Div();
        formCard.addClassName("st-login-form-card");

        H2 formTitle = new H2("Terminal Access");
        formTitle.addClassName("st-login-form-title");

        Paragraph formSubtitle = new Paragraph("Enter your credentials to access the trading terminal.");
        formSubtitle.addClassName("st-login-form-subtitle");

        TextField username = new TextField("Username");
        username.setPlaceholder("Enter username");
        username.setWidthFull();

        PasswordField password = new PasswordField("Password");
        password.setPlaceholder("Enter password");
        password.setWidthFull();

        Span errorMsg = new Span("Invalid username or password.");
        errorMsg.getStyle()
                .set("color", "var(--st-red)")
                .set("font-size", "12px")
                .set("display", "none")
                .set("margin-top", "4px");

        Button loginButton = new Button("Authorize Access");
        loginButton.setWidthFull();
        loginButton.addClassNames("st-btn", "st-btn-primary");
        loginButton.getElement().removeAttribute("theme");

        loginButton.addClickListener(e -> {
            errorMsg.getStyle().set("display", "none");
            loginButton.setEnabled(false);
            loginButton.setText("Authenticating...");

            SecurityService securityService = new SecurityService();
            try {
                securityService.authenticate(username.getValue(), password.getValue());
                UI.getCurrent().navigate("ui/dashboard");
            } catch (AuthenticationException ex) {
                errorMsg.getStyle().set("display", "block");
                loginButton.setEnabled(true);
                loginButton.setText("Authorize Access");
            }
        });

        loginButton.addClickShortcut(Key.ENTER);

        RouterLink registerLink = new RouterLink("Create a new trader account →", RegistrationView.class);
        registerLink.getStyle().set("font-size", "13px").set("display", "block").set("text-align", "center").set("margin-top", "12px").set("color", "var(--st-copper)").set("text-decoration", "none");

        VerticalLayout fields = new VerticalLayout(username, password, errorMsg, loginButton, registerLink);
        fields.setPadding(false);
        fields.setSpacing(true);

        Div footer = new Div();
        footer.addClassName("st-login-footer");
        footer.setText("SimTrade Terminal · Paper Trading Only");

        formCard.add(formTitle, formSubtitle, fields, footer);
        formPanel.add(formCard);

        add(brandPanel, formPanel);
    }

    private Component createFeatureItem(String text) {
        Div item = new Div();
        item.addClassName("st-login-feature-item");
        Span icon = new Span();
        icon.getElement().setProperty("innerHTML", SVG_CHECK);
        icon.getStyle().set("width", "16px").set("height", "16px").set("display", "inline-flex").set("color", "var(--st-green)");
        Span label = new Span(text);
        item.add(icon, label);
        return item;
    }

    private Component createSvgIcon(String svgData) {
        Span span = new Span();
        span.getElement().setProperty("innerHTML", svgData);
        span.getStyle().set("display", "inline-flex").set("align-items", "center").set("justify-content", "center");
        return span;
    }
}
