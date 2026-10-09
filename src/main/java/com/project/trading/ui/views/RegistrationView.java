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
@PageTitle("Sign Up | SimTrade")
public class RegistrationView extends Div {

    private static final String SVG_BRAND = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z\" /></svg>";

    public RegistrationView() {
        addClassName("st-login-page");

        // === Left brand panel ===
        Div brandPanel = new Div();
        brandPanel.addClassName("st-login-brand-panel");

        Div brandLogo = new Div();
        brandLogo.addClassName("st-login-brand-logo");
        Component brandIcon = createSvgIcon(SVG_BRAND);
        Span brandName = new Span("SimTrade");
        brandName.addClassName("st-login-brand-name");
        brandLogo.add(brandIcon, brandName);

        Div heading = new Div();
        heading.addClassName("st-login-brand-heading");
        heading.setText("Start your journey.");

        Paragraph desc = new Paragraph("Create an account to start practicing with ₹100,000 virtual cash.");
        desc.addClassName("st-login-brand-text");

        brandPanel.add(brandLogo, heading, desc);

        // === Right form panel ===
        Div formPanel = new Div();
        formPanel.addClassName("st-login-form-panel");

        Div formCard = new Div();
        formCard.addClassName("st-login-form-card");

        H2 formTitle = new H2("Create an account");
        formTitle.addClassName("st-login-form-title");

        Paragraph formSubtitle = new Paragraph("Join SimTrade today.");
        formSubtitle.addClassName("st-login-form-subtitle");

        TextField fullName = new TextField("Full Name");
        fullName.setWidthFull();

        TextField username = new TextField("Username");
        username.setWidthFull();

        EmailField email = new EmailField("Email");
        email.setWidthFull();

        PasswordField password = new PasswordField("Password");
        password.setWidthFull();

        PasswordField confirmPassword = new PasswordField("Confirm Password");
        confirmPassword.setWidthFull();

        Span errorMsg = new Span();
        errorMsg.getStyle()
                .set("color", "var(--st-red)")
                .set("font-size", "13px")
                .set("display", "none")
                .set("margin-top", "4px");
                
        Span successMsg = new Span();
        successMsg.getStyle()
                .set("color", "var(--st-green)")
                .set("font-size", "13px")
                .set("display", "none")
                .set("margin-top", "4px");

        Button registerButton = new Button("Sign up");
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
                UI.getCurrent().getPage().executeJs("setTimeout(() => window.location.href='login', 2000)");
            } catch (ValidationException ex) {
                errorMsg.setText(ex.getMessage());
                errorMsg.getStyle().set("display", "block");
                registerButton.setEnabled(true);
                registerButton.setText("Sign up");
            } catch (Exception ex) {
                errorMsg.setText("Registration failed due to an internal error.");
                errorMsg.getStyle().set("display", "block");
                registerButton.setEnabled(true);
                registerButton.setText("Sign up");
            }
        });

        registerButton.addClickShortcut(Key.ENTER);
        
        RouterLink loginLink = new RouterLink("Already have an account? Sign in", LoginView.class);
        loginLink.getStyle().set("font-size", "14px").set("display", "block").set("margin-top", "16px").set("text-align", "center");

        VerticalLayout fields = new VerticalLayout(fullName, username, email, password, confirmPassword, errorMsg, successMsg, registerButton, loginLink);
        fields.setPadding(false);
        fields.setSpacing(false); // We can rely on default spacing or adjust
        fields.getThemeList().add("spacing-s");

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
