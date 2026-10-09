package com.project.trading.ui.admin;

import com.project.trading.exception.ValidationException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.User;
import com.project.trading.model.UserStatus;
import com.project.trading.service.UserService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "admin/users", layout = AdminLayout.class)
@PageTitle("User Management | Admin")
public class AdminUsersView extends VerticalLayout {

    private final UserService userService = new UserService();
    private final SecurityService securityService = new SecurityService();
    private final Grid<User> grid = new Grid<>(User.class, false);
    private final TextField filterText = new TextField();
    
    private List<User> allUsers;

    public AdminUsersView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("User Management");
        title.getStyle().set("margin-top", "0").set("color", "var(--st-text-main)");
        
        configureGrid();
        
        filterText.setPlaceholder("Search by username or email...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.setWidth("300px");

        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.getStyle().set("margin-bottom", "16px");

        add(title, toolbar, grid);
        
        refreshData();
    }

    private void configureGrid() {
        grid.addColumn(User::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(User::getUsername).setHeader("Username").setSortable(true);
        grid.addColumn(User::getEmail).setHeader("Email").setSortable(true);
        grid.addColumn(User::getFullName).setHeader("Full Name").setSortable(true);
        
        grid.addComponentColumn(user -> {
            Span roleSpan = new Span(user.getRole().name());
            if (user.getRole() == com.project.trading.model.Role.ADMIN) {
                roleSpan.getStyle().set("color", "var(--st-primary-color)").set("font-weight", "bold");
            }
            return roleSpan;
        }).setHeader("Role").setSortable(true);
        
        grid.addComponentColumn(user -> {
            Span statusSpan = new Span(user.getStatus().name());
            statusSpan.getStyle().set("padding", "4px 8px").set("border-radius", "4px").set("font-size", "0.85rem");
            if (user.getStatus() == UserStatus.ACTIVE) {
                statusSpan.getStyle().set("background", "rgba(16, 185, 129, 0.1)").set("color", "var(--st-success-color)");
            } else {
                statusSpan.getStyle().set("background", "rgba(239, 68, 68, 0.1)").set("color", "var(--st-danger-color)");
            }
            return statusSpan;
        }).setHeader("Status").setSortable(true);
        
        grid.addComponentColumn(user -> {
            HorizontalLayout actions = new HorizontalLayout();
            AuthUser currentUser = securityService.getAuthenticatedUser();
            
            if (currentUser != null && currentUser.getId() != user.getId() && user.getRole() != com.project.trading.model.Role.ADMIN) {
                boolean isBlocked = user.getStatus() == UserStatus.BLOCKED;
                Button toggleBtn = new Button(isBlocked ? "Unblock" : "Block");
                toggleBtn.getStyle()
                         .set("background", isBlocked ? "var(--st-success-color)" : "var(--st-danger-color)")
                         .set("color", "white");
                
                toggleBtn.addClickListener(e -> confirmToggleStatus(user, currentUser.getId(), !isBlocked));
                actions.add(toggleBtn);
            }
            return actions;
        }).setHeader("Actions");
        
        grid.addThemeName("row-stripes");
    }

    private void refreshData() {
        allUsers = userService.listAll();
        updateList();
    }

    private void updateList() {
        if (allUsers == null) return;
        
        String filter = filterText.getValue() != null ? filterText.getValue().toLowerCase().trim() : "";
        if (filter.isEmpty()) {
            grid.setItems(allUsers);
        } else {
            List<User> filtered = allUsers.stream()
                .filter(u -> u.getUsername().toLowerCase().contains(filter) 
                          || u.getEmail().toLowerCase().contains(filter)
                          || u.getFullName().toLowerCase().contains(filter))
                .collect(Collectors.toList());
            grid.setItems(filtered);
        }
    }

    private void confirmToggleStatus(User user, long adminId, boolean block) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(block ? "Confirm Block" : "Confirm Unblock");
        
        Paragraph text = new Paragraph("Are you sure you want to " + (block ? "block" : "unblock") + 
                                       " user '" + user.getUsername() + "'?");
        dialog.add(text);
        
        Button confirm = new Button("Confirm", e -> {
            try {
                userService.setBlocked(adminId, user.getId(), block);
                showSuccess("User " + user.getUsername() + " successfully " + (block ? "blocked" : "unblocked") + ".");
                refreshData();
                dialog.close();
            } catch (Exception ex) {
                showError("Failed to update user status: " + ex.getMessage());
            }
        });
        confirm.getStyle().set("background", "var(--st-primary-color)").set("color", "white");
        
        Button cancel = new Button("Cancel", e -> dialog.close());
        
        dialog.getFooter().add(cancel, confirm);
        dialog.open();
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
