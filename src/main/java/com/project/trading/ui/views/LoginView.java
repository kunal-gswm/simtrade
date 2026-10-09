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

@Route("login")
@PageTitle("Login | SimTrade")
public class LoginView extends Div {

    private static final String SVG_BRAND = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z\" /></svg>";

    public LoginView() {
        addClassName("st-login-page");

        // === Left brand panel ===
        Div brandPanel = new Div();
        brandPanel.addClassName("st-login-brand-panel");

        // Brand logo
        Div brandLogo = new Div();
        brandLogo.addClassName("st-login-brand-logo");
        Component brandIcon = createSvgIcon(SVG_BRAND);
        Span brandName = new Span("SimTrade");
        brandName.addClassName("st-login-brand-name");
        brandLogo.add(brandIcon, brandName);

        // Heading
        Div heading = new Div();
        heading.addClassName("st-login-brand-heading");
        heading.setText("Practice trading. Learn by doing.");

        // Description
        Paragraph desc = new Paragraph("Explore stocks, place simulated orders, and track your portfolio.");
        desc.addClassName("st-login-brand-text");

        brandPanel.add(brandLogo, heading, desc);

        // === Right form panel ===
        Div formPanel = new Div();
        formPanel.addClassName("st-login-form-panel");

        Div formCard = new Div();
        formCard.addClassName("st-login-form-card");

        H2 formTitle = new H2("Welcome back");
        formTitle.addClassName("st-login-form-title");

        Paragraph formSubtitle = new Paragraph("Sign in to continue to SimTrade.");
        formSubtitle.addClassName("st-login-form-subtitle");

        TextField username = new TextField("Username");
        username.setPlaceholder("Enter your username");
        username.setWidthFull();

        PasswordField password = new PasswordField("Password");
        password.setPlaceholder("Enter your password");
        password.setWidthFull();

        Span errorMsg = new Span("Invalid username or password.");
        errorMsg.getStyle()
                .set("color", "var(--st-red)")
                .set("font-size", "13px")
                .set("display", "none")
                .set("margin-top", "4px");

        Button loginButton = new Button("Sign in");
        loginButton.setWidthFull();
        loginButton.addClassNames("st-btn", "st-btn-primary");
        loginButton.getElement().removeAttribute("theme");

        loginButton.addClickListener(e -> {
            errorMsg.getStyle().set("display", "none");
            loginButton.setEnabled(false);
            loginButton.setText("Signing in...");

            SecurityService securityService = new SecurityService();
            try {
                securityService.authenticate(username.getValue(), password.getValue());
                UI.getCurrent().navigate("");
            } catch (AuthenticationException ex) {
                errorMsg.getStyle().set("display", "block");
                loginButton.setEnabled(true);
                loginButton.setText("Sign in");
            }
        });

        loginButton.addClickShortcut(Key.ENTER);

        VerticalLayout fields = new VerticalLayout(username, password, errorMsg, loginButton);
        fields.setPadding(false);
        fields.setSpacing(true);

        Div footer = new Div();
        footer.addClassName("st-login-footer");
        footer.setText("SimTrade · Academic Project");

        formCard.add(formTitle, formSubtitle, fields, footer);
        formPanel.add(formCard);

        add(brandPanel, formPanel);
    }

    private Component createSvgIcon(String svgData) {
        Span span = new Span();
        span.getElement().setProperty("innerHTML", svgData);
        span.getStyle().set("display", "inline-flex").set("align-items", "center").set("justify-content", "center");
        return span;
    }
}
