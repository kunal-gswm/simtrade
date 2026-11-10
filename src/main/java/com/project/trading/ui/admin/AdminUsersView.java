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
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
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
@PageTitle("User Management | SimTrade Admin")
public class AdminUsersView extends VerticalLayout {

    private final UserService userService = new UserService();
    private final SecurityService securityService = new SecurityService();
    private final Grid<User> grid = new Grid<>(User.class, false);
    private final TextField filterText = new TextField();

    private List<User> allUsers;

    public AdminUsersView() {
        addClassName("st-page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        Div headerContainer = new Div();
        headerContainer.addClassName("st-header-container");

        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");

        H1 title = new H1("User & Account Management");
        title.addClassName("st-page-title");
        Paragraph subtitle = new Paragraph("Inspect registered trader accounts, manage access permissions, and revoke privileges.");
        subtitle.addClassName("st-page-subtitle");

        titleGroup.add(title, subtitle);
        headerContainer.add(titleGroup);
        add(headerContainer);

        configureGrid();

        filterText.setPlaceholder("Search username, email or full name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.addClassName("st-search-field");
        filterText.setWidth("min(320px, 100%)");

        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.getStyle().set("margin-bottom", "16px");

        Div tableSurface = new Div(grid);
        tableSurface.addClassName("st-table-surface");
        tableSurface.setWidthFull();

        add(toolbar, tableSurface);

        refreshData();
    }

    private void configureGrid() {
        grid.addColumn(User::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(User::getUsername).setHeader("Username").setSortable(true);
        grid.addColumn(User::getEmail).setHeader("Email").setSortable(true);
        grid.addColumn(User::getFullName).setHeader("Full Name").setSortable(true);

        grid.addComponentColumn(user -> {
            Span roleSpan = new Span(user.getRole().name());
            roleSpan.addClassName("st-badge");
            if (user.getRole() == com.project.trading.model.Role.ADMIN) {
                roleSpan.getStyle().set("color", "var(--st-amber)").set("border", "1px solid rgba(217, 119, 6, 0.3)").set("background", "var(--st-amber-bg)");
            } else {
                roleSpan.addClassName("st-badge-sector");
            }
            return roleSpan;
        }).setHeader("Role").setSortable(true);

        grid.addComponentColumn(user -> {
            Span statusSpan = new Span(user.getStatus().name());
            statusSpan.addClassName("st-badge");
            if (user.getStatus() == UserStatus.ACTIVE) {
                statusSpan.addClassName("st-badge-buy");
            } else {
                statusSpan.addClassName("st-badge-sell");
            }
            return statusSpan;
        }).setHeader("Status").setSortable(true);

        grid.addComponentColumn(user -> {
            HorizontalLayout actions = new HorizontalLayout();
            AuthUser currentUser = securityService.getAuthenticatedUser();

            if (currentUser != null && currentUser.getId() != user.getId() && user.getRole() != com.project.trading.model.Role.ADMIN) {
                boolean isBlocked = user.getStatus() == UserStatus.BLOCKED;
                Button toggleBtn = new Button(isBlocked ? "Unblock" : "Block");
                toggleBtn.addClassNames("st-btn", isBlocked ? "st-btn-buy" : "st-btn-sell");
                toggleBtn.getElement().removeAttribute("theme");
                toggleBtn.getStyle().set("height", "26px").set("font-size", "11px").set("padding", "0 10px");

                toggleBtn.addClickListener(e -> confirmToggleStatus(user, currentUser.getId(), !isBlocked));
                actions.add(toggleBtn);
            }
            return actions;
        }).setHeader("Actions");

        grid.setAllRowsVisible(true);
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
                            || (u.getEmail() != null && u.getEmail().toLowerCase().contains(filter))
                            || (u.getFullName() != null && u.getFullName().toLowerCase().contains(filter)))
                    .collect(Collectors.toList());
            grid.setItems(filtered);
        }
    }

    private void confirmToggleStatus(User user, long adminId, boolean block) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(block ? "Confirm Block Access" : "Confirm Restore Access");
        dialog.setWidth("min(400px, 92vw)");

        Paragraph text = new Paragraph("Are you sure you want to " + (block ? "block" : "unblock") +
                " user '" + user.getUsername() + "'? " +
                (block ? "The user will be immediately barred from placing trades." : "The user will regain terminal access."));
        text.getStyle().set("color", "var(--st-text-secondary)").set("font-size", "13px").set("line-height", "1.5");
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
        confirm.addClassNames("st-btn", block ? "st-btn-sell" : "st-btn-buy");
        confirm.getElement().removeAttribute("theme");

        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addClassNames("st-btn", "st-btn-secondary");
        cancel.getElement().removeAttribute("theme");

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
