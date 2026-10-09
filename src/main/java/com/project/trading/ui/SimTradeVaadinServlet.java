package com.project.trading.ui;

import com.vaadin.flow.server.VaadinServlet;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = {"/*"}, name = "SimTradeVaadinServlet", asyncSupported = true)
public class SimTradeVaadinServlet extends VaadinServlet {
}
