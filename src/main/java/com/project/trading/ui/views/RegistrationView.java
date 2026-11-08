package com.project.trading.ui.views;

import com.project.trading.exception.ValidationException;
import com.project.trading.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("register")
@PageTitle("Trader Onboarding | SimTrade")
public class RegistrationView extends Div {

    private static final String SVG_BRAND = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z\" /></svg>";
    private static final String SVG_CHECK = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M4.5 12.75l6 6 9-13.5\" /></svg>";

    public RegistrationView() {
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
        heading.setText("Open a Paper Trading Account.");

        Paragraph desc = new Paragraph("Join the simulation environment and receive ₹100,000 in virtual sandbox capital. No financial risk, zero broker fees, complete execution transparency.");
        desc.addClassName("st-login-brand-text");

        Div featureList = new Div();
        featureList.addClassName("st-login-feature-list");
        featureList.add(
                createFeatureItem("Zero risk · 100% paper trading simulation"),
                createFeatureItem("Full access to live market quote simulator"),
                createFeatureItem("Instant account provisioning with ₹100,000 balance")
        );

        brandPanel.add(brandLogo, heading, desc, featureList);

        // === Right Form Panel ===
        Div formPanel = new Div();
        formPanel.addClassName("st-login-form-panel");

        Div formCard = new Div();
        formCard.addClassName("st-login-form-card");

        H2 formTitle = new H2("Trader Onboarding");
        formTitle.addClassName("st-login-form-title");

        Paragraph formSubtitle = new Paragraph("Set up your paper trading terminal credentials.");
        formSubtitle.addClassName("st-login-form-subtitle");

        TextField fullName = new TextField("Full Legal Name");
        fullName.setWidthFull();

        TextField username = new TextField("Username");
        username.setWidthFull();

        EmailField email = new EmailField("Email Address");
        email.setWidthFull();

        PasswordField password = new PasswordField("Password");
        password.setWidthFull();

        PasswordField confirmPassword = new PasswordField("Confirm Password");
        confirmPassword.setWidthFull();

        Span errorMsg = new Span();
        errorMsg.getStyle()
                .set("color", "var(--st-red)")
                .set("font-size", "12px")
                .set("display", "none")
                .set("margin-top", "4px");

        Span successMsg = new Span();
        successMsg.getStyle()
                .set("color", "var(--st-green)")
                .set("font-size", "12px")
                .set("display", "none")
                .set("margin-top", "4px");

        Button registerButton = new Button("Create Account");
        registerButton.setWidthFull();
        registerButton.addClassNames("st-btn", "st-btn-primary");
        registerButton.getElement().removeAttribute("theme");

        registerButton.addClickListener(e -> {
            errorMsg.getStyle().set("display", "none");
            successMsg.getStyle().set("display", "none");

            if (!password.getValue().equals(confirmPassword.getValue())) {
                errorMsg.setText("Passwords do not match.");
                errorMsg.getStyle().set("display", "block");
                return;
            }

            registerButton.setEnabled(false);
            registerButton.setText("Creating account...");

            UserService userService = new UserService();
            try {
                userService.register(username.getValue(), email.getValue(), fullName.getValue(), password.getValue());
                successMsg.setText("Registration successful! Redirecting to login...");
                successMsg.getStyle().set("display", "block");
                UI.getCurrent().getPage().executeJs("setTimeout(() => window.location.href='login', 1500)");
            } catch (ValidationException ex) {
                errorMsg.setText(ex.getMessage());
                errorMsg.getStyle().set("display", "block");
                registerButton.setEnabled(true);
                registerButton.setText("Create Account");
            } catch (Exception ex) {
                errorMsg.setText("Registration failed due to a server error.");
                errorMsg.getStyle().set("display", "block");
                registerButton.setEnabled(true);
                registerButton.setText("Create Account");
            }
        });

        registerButton.addClickShortcut(Key.ENTER);

        RouterLink loginLink = new RouterLink("Already registered? Sign in →", LoginView.class);
        loginLink.getStyle().set("font-size", "13px").set("display", "block").set("margin-top", "14px").set("text-align", "center").set("color", "var(--st-blue)").set("text-decoration", "none");

        VerticalLayout fields = new VerticalLayout(fullName, username, email, password, confirmPassword, errorMsg, successMsg, registerButton, loginLink);
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
