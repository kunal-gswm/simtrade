package com.project.trading.ui.admin;

import com.vaadin.flow.router.Route;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminSecurityTest {

    @Test
    public void testAdminRoutesAreProperlyPrefixed() {
        Class<?>[] adminViews = {
                AdminDashboardView.class,
                AdminUsersView.class,
                AdminStocksView.class,
                AdminToolsView.class,
                AdminTradeMonitorView.class
        };

        for (Class<?> view : adminViews) {
            Route route = view.getAnnotation(Route.class);
            assertTrue(route.value().startsWith("admin"), 
                       view.getSimpleName() + " must have a route starting with 'admin' for AuthInitListener to protect it.");
            assertEquals(AdminLayout.class, route.layout(), 
                         view.getSimpleName() + " must use AdminLayout.");
        }
    }
}
