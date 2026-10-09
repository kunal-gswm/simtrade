package com.project.trading.ui.views;

import com.vaadin.flow.router.Route;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DashboardViewTest {

    @Test
    public void testDashboardRoute() {
        Route route = DashboardView.class.getAnnotation(Route.class);
        assertNotNull(route, "DashboardView should have a @Route annotation");
        assertEquals("ui/dashboard", route.value(), "Dashboard route should be 'ui/dashboard'");
        assertEquals(MainLayout.class, route.layout(), "Dashboard should use MainLayout");
    }
}
