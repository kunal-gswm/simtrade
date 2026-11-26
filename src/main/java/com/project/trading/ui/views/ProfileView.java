package com.project.trading.ui.views;

import com.project.trading.exception.AuthenticationException;
import com.project.trading.exception.UserNotFoundException;
import com.project.trading.exception.ValidationException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.User;
import com.project.trading.service.UserService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
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

import java.text.NumberFormat;
import java.util.Locale;

@Route(value = "ui/profile", layout = MainLayout.class)
@PageTitle("Account Settings | SimTrade")
public class ProfileView extends Div {

    private final UserService userService;
    private final SecurityService securityService;
    private final NumberFormat currencyFormat;
    private User currentUser;

    public ProfileView() {
        this.userService = new UserService();
        this.securityService = new SecurityService();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        addClassName("st-page");

        AuthUser authUser = securityService.getAuthenticatedUser();
        if (authUser == null) {
            return;
        }

        try {
            currentUser = userService.getById(authUser.getId());
        } catch (UserNotFoundException e) {
            return;
        }

        Div headerContainer = new Div();
        headerContainer.addClassName("st-header-container");

        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");
        H2 pageTitle = new H2("Account & Security");
        pageTitle.addClassName("st-page-title");
        Paragraph subtitle = new Paragraph("Trader identity, simulation virtual balance, and access credentials.");
        subtitle.addClassName("st-page-subtitle");
        titleGroup.add(pageTitle, subtitle);

        headerContainer.add(titleGroup);
        add(headerContainer);

        Div card = new Div();
        card.addClassNames("st-table-surface", "st-card-padded");
        card.getStyle().set("max-width", "640px").set("width", "100%").set("box-sizing", "border-box");

        // Profile Details
        H2 secTitle = new H2("Trader Profile");
        secTitle.addClassName("st-section-title");

        FormLayout profileForm = new FormLayout();

        TextField usernameField = new TextField("Username");
        usernameField.setValue(currentUser.getUsername());
        usernameField.setReadOnly(true);

        TextField cashBalanceField = new TextField("Virtual Cash Balance");
        cashBalanceField.setValue(currencyFormat.format(currentUser.getCashBalance()));
        cashBalanceField.setReadOnly(true);

        TextField fullNameField = new TextField("Full Legal Name");
        fullNameField.setValue(currentUser.getFullName() != null ? currentUser.getFullName() : "");

        EmailField emailField = new EmailField("Email Address");
        emailField.setValue(currentUser.getEmail() != null ? currentUser.getEmail() : "");

        profileForm.add(usernameField, cashBalanceField, fullNameField, emailField);
        profileForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("480px", 2)
        );

        Span profileMsg = new Span();
        profileMsg.getStyle().set("font-size", "13px").set("display", "none").set("margin-top", "8px");

        Button updateProfileButton = new Button("Save Profile Changes");
        updateProfileButton.addClassNames("st-btn", "st-btn-primary");
        updateProfileButton.getElement().removeAttribute("theme");
        updateProfileButton.getStyle().set("margin-top", "16px");

        updateProfileButton.addClickListener(e -> {
            profileMsg.getStyle().set("display", "none");
            try {
                userService.updateProfile(currentUser.getId(), fullNameField.getValue(), emailField.getValue());
                profileMsg.setText("Profile updated successfully.");
                profileMsg.getStyle().set("color", "var(--st-jade)").set("display", "block");
            } catch (ValidationException ex) {
                profileMsg.setText(ex.getMessage());
                profileMsg.getStyle().set("color", "var(--st-coral)").set("display", "block");
            } catch (Exception ex) {
                profileMsg.setText("Failed to update profile.");
                profileMsg.getStyle().set("color", "var(--st-coral)").set("display", "block");
            }
        });

        // Change Password Section
        H2 pwdTitle = new H2("Security & Password");
        pwdTitle.addClassName("st-section-title");
        pwdTitle.getStyle().set("margin-top", "36px").set("padding-top", "24px").set("border-top", "1px solid var(--st-border-subtle)");

        FormLayout pwdForm = new FormLayout();

        PasswordField currentPasswordField = new PasswordField("Current Password");
        PasswordField newPasswordField = new PasswordField("New Password");
        PasswordField confirmNewPasswordField = new PasswordField("Confirm New Password");

        pwdForm.add(currentPasswordField, newPasswordField, confirmNewPasswordField);
        pwdForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        Span pwdMsg = new Span();
        pwdMsg.getStyle().set("font-size", "13px").set("display", "none").set("margin-top", "8px");

        Button changePwdButton = new Button("Update Password");
        changePwdButton.addClassNames("st-btn", "st-btn-secondary");
        changePwdButton.getElement().removeAttribute("theme");
        changePwdButton.getStyle().set("margin-top", "16px");

        changePwdButton.addClickListener(e -> {
            pwdMsg.getStyle().set("display", "none");

            if (!newPasswordField.getValue().equals(confirmNewPasswordField.getValue())) {
                pwdMsg.setText("New passwords do not match.");
                pwdMsg.getStyle().set("color", "var(--st-coral)").set("display", "block");
                return;
            }

            try {
                userService.changePassword(currentUser.getId(), currentPasswordField.getValue(), newPasswordField.getValue());
                pwdMsg.setText("Password successfully changed.");
                pwdMsg.getStyle().set("color", "var(--st-jade)").set("display", "block");
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmNewPasswordField.clear();
            } catch (ValidationException | AuthenticationException ex) {
                pwdMsg.setText(ex.getMessage());
                pwdMsg.getStyle().set("color", "var(--st-coral)").set("display", "block");
            } catch (Exception ex) {
                pwdMsg.setText("Failed to update password.");
                pwdMsg.getStyle().set("color", "var(--st-coral)").set("display", "block");
            }
        });

        VerticalLayout layout = new VerticalLayout(secTitle, profileForm, updateProfileButton, profileMsg, pwdTitle, pwdForm, changePwdButton, pwdMsg);
        layout.setPadding(false);
        layout.setSpacing(false);

        card.add(layout);
        add(card);
    }
}
