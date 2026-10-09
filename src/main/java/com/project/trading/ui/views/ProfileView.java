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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "ui/profile", layout = MainLayout.class)
@PageTitle("Profile | SimTrade")
public class ProfileView extends Div {

    private final UserService userService;
    private final SecurityService securityService;
    private User currentUser;

    public ProfileView() {
        this.userService = new UserService();
        this.securityService = new SecurityService();
        
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

        Div content = new Div();
        content.addClassName("st-table-surface");
        content.getStyle().set("max-width", "600px").set("margin", "0 auto").set("padding", "32px");

        H2 title = new H2("Account Profile");
        title.addClassName("st-page-title");
        title.getStyle().set("margin-bottom", "24px");

        FormLayout profileForm = new FormLayout();
        
        TextField usernameField = new TextField("Username");
        usernameField.setValue(currentUser.getUsername());
        usernameField.setReadOnly(true);
        
        TextField cashBalanceField = new TextField("Virtual Cash Balance");
        cashBalanceField.setValue("₹" + currentUser.getCashBalance().toString());
        cashBalanceField.setReadOnly(true);

        TextField fullNameField = new TextField("Full Name");
        fullNameField.setValue(currentUser.getFullName() != null ? currentUser.getFullName() : "");
        
        EmailField emailField = new EmailField("Email");
        emailField.setValue(currentUser.getEmail() != null ? currentUser.getEmail() : "");

        profileForm.add(usernameField, cashBalanceField, fullNameField, emailField);
        profileForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("400px", 2));

        Span profileMsg = new Span();
        profileMsg.getStyle().set("font-size", "14px").set("display", "none").set("margin-top", "8px");

        Button updateProfileButton = new Button("Update Profile");
        updateProfileButton.addClassNames("st-btn", "st-btn-primary");
        updateProfileButton.getStyle().set("margin-top", "16px");
        updateProfileButton.addClickListener(e -> {
            profileMsg.getStyle().set("display", "none");
            try {
                userService.updateProfile(currentUser.getId(), fullNameField.getValue(), emailField.getValue());
                profileMsg.setText("Profile updated successfully.");
                profileMsg.getStyle().set("color", "var(--st-green)").set("display", "block");
            } catch (ValidationException ex) {
                profileMsg.setText(ex.getMessage());
                profileMsg.getStyle().set("color", "var(--st-red)").set("display", "block");
            } catch (Exception ex) {
                profileMsg.setText("Failed to update profile.");
                profileMsg.getStyle().set("color", "var(--st-red)").set("display", "block");
            }
        });

        H2 pwdTitle = new H2("Change Password");
        pwdTitle.addClassName("st-section-title");
        pwdTitle.getStyle().set("margin-top", "40px").set("margin-bottom", "16px");

        FormLayout pwdForm = new FormLayout();
        
        PasswordField currentPasswordField = new PasswordField("Current Password");
        PasswordField newPasswordField = new PasswordField("New Password");
        PasswordField confirmNewPasswordField = new PasswordField("Confirm New Password");

        pwdForm.add(currentPasswordField, newPasswordField, confirmNewPasswordField);
        pwdForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        Span pwdMsg = new Span();
        pwdMsg.getStyle().set("font-size", "14px").set("display", "none").set("margin-top", "8px");

        Button changePwdButton = new Button("Change Password");
        changePwdButton.addClassNames("st-btn", "st-btn-secondary"); 
        changePwdButton.getStyle().set("margin-top", "16px");
        changePwdButton.addClickListener(e -> {
            pwdMsg.getStyle().set("display", "none");
            
            if (!newPasswordField.getValue().equals(confirmNewPasswordField.getValue())) {
                pwdMsg.setText("New passwords do not match.");
                pwdMsg.getStyle().set("color", "var(--st-red)").set("display", "block");
                return;
            }

            try {
                userService.changePassword(currentUser.getId(), currentPasswordField.getValue(), newPasswordField.getValue());
                pwdMsg.setText("Password changed successfully.");
                pwdMsg.getStyle().set("color", "var(--st-green)").set("display", "block");
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmNewPasswordField.clear();
            } catch (ValidationException | AuthenticationException ex) {
                pwdMsg.setText(ex.getMessage());
                pwdMsg.getStyle().set("color", "var(--st-red)").set("display", "block");
            } catch (Exception ex) {
                pwdMsg.setText("Failed to change password.");
                pwdMsg.getStyle().set("color", "var(--st-red)").set("display", "block");
            }
        });

        VerticalLayout layout = new VerticalLayout(title, profileForm, updateProfileButton, profileMsg, pwdTitle, pwdForm, changePwdButton, pwdMsg);
        layout.setPadding(false);

        content.add(layout);
        add(content);
    }
}
