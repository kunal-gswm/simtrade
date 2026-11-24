package com.project.trading.order;

import com.project.trading.exception.InvalidOrderException;
import com.project.trading.order.model.OrderStatus;
import com.project.trading.order.service.OrderStateMachine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderStateMachineTest {

    @Test
    public void testLegalTransitionsFromPending() {
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.PENDING, OrderStatus.OPEN));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.PENDING, OrderStatus.TRIGGER_PENDING));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.PENDING, OrderStatus.PARTIALLY_FILLED));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.PENDING, OrderStatus.FILLED));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.PENDING, OrderStatus.REJECTED));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.PENDING, OrderStatus.CANCELLED));

        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderStatus.PENDING, OrderStatus.OPEN, 101L));
        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderStatus.PENDING, OrderStatus.TRIGGER_PENDING, 102L));
        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderStatus.PENDING, OrderStatus.CANCELLED, 103L));
    }

    @Test
    public void testLegalTransitionsFromOpen() {
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.OPEN, OrderStatus.FILLED));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.OPEN, OrderStatus.CANCELLED));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.OPEN, OrderStatus.EXPIRED));

        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderStatus.OPEN, OrderStatus.FILLED, 201L));
        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderStatus.OPEN, OrderStatus.CANCELLED, 202L));
    }

    @Test
    public void testLegalTransitionsFromTriggerPending() {
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.TRIGGER_PENDING, OrderStatus.OPEN));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.TRIGGER_PENDING, OrderStatus.PARTIALLY_FILLED));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.TRIGGER_PENDING, OrderStatus.FILLED));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.TRIGGER_PENDING, OrderStatus.CANCELLED));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.TRIGGER_PENDING, OrderStatus.EXPIRED));

        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderStatus.TRIGGER_PENDING, OrderStatus.OPEN, 301L));
        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderStatus.TRIGGER_PENDING, OrderStatus.CANCELLED, 302L));
    }

    @Test
    public void testLegalTransitionsFromPartiallyFilled() {
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.PARTIALLY_FILLED, OrderStatus.FILLED));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.PARTIALLY_FILLED, OrderStatus.CANCELLED));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.PARTIALLY_FILLED, OrderStatus.EXPIRED));

        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderStatus.PARTIALLY_FILLED, OrderStatus.FILLED, 401L));
    }

    @Test
    public void testSameStateIdempotency() {
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.OPEN, OrderStatus.OPEN));
        assertTrue(OrderStateMachine.isValidTransition(OrderStatus.FILLED, OrderStatus.FILLED));
        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderStatus.OPEN, OrderStatus.OPEN, 501L));
    }

    @Test
    public void testIllegalTransitionsFromTerminalStates() {
        // FILLED is terminal - cannot cancel or reopen
        assertFalse(OrderStateMachine.isValidTransition(OrderStatus.FILLED, OrderStatus.CANCELLED));
        assertFalse(OrderStateMachine.isValidTransition(OrderStatus.FILLED, OrderStatus.OPEN));
        assertFalse(OrderStateMachine.isValidTransition(OrderStatus.FILLED, OrderStatus.PARTIALLY_FILLED));
        assertThrows(InvalidOrderException.class, () ->
                OrderStateMachine.validateTransition(OrderStatus.FILLED, OrderStatus.CANCELLED, 601L));

        // CANCELLED is terminal
        assertFalse(OrderStateMachine.isValidTransition(OrderStatus.CANCELLED, OrderStatus.OPEN));
        assertFalse(OrderStateMachine.isValidTransition(OrderStatus.CANCELLED, OrderStatus.FILLED));
        assertThrows(InvalidOrderException.class, () ->
                OrderStateMachine.validateTransition(OrderStatus.CANCELLED, OrderStatus.OPEN, 602L));

        // REJECTED is terminal
        assertFalse(OrderStateMachine.isValidTransition(OrderStatus.REJECTED, OrderStatus.OPEN));
        assertFalse(OrderStateMachine.isValidTransition(OrderStatus.REJECTED, OrderStatus.FILLED));
        assertThrows(InvalidOrderException.class, () ->
                OrderStateMachine.validateTransition(OrderStatus.REJECTED, OrderStatus.OPEN, 603L));

        // EXPIRED is terminal
        assertFalse(OrderStateMachine.isValidTransition(OrderStatus.EXPIRED, OrderStatus.OPEN));
        assertFalse(OrderStateMachine.isValidTransition(OrderStatus.EXPIRED, OrderStatus.FILLED));
        assertThrows(InvalidOrderException.class, () ->
                OrderStateMachine.validateTransition(OrderStatus.EXPIRED, OrderStatus.OPEN, 604L));
    }

    @Test
    public void testTerminalAndActiveStateChecks() {
        assertTrue(OrderStatus.FILLED.isTerminal());
        assertTrue(OrderStatus.CANCELLED.isTerminal());
        assertTrue(OrderStatus.REJECTED.isTerminal());
        assertTrue(OrderStatus.EXPIRED.isTerminal());

        assertFalse(OrderStatus.PENDING.isTerminal());
        assertFalse(OrderStatus.OPEN.isTerminal());
        assertFalse(OrderStatus.TRIGGER_PENDING.isTerminal());
        assertFalse(OrderStatus.PARTIALLY_FILLED.isTerminal());

        assertTrue(OrderStatus.OPEN.isActive());
        assertTrue(OrderStatus.PENDING.isActive());
        assertTrue(OrderStatus.TRIGGER_PENDING.isActive());
        assertTrue(OrderStatus.PARTIALLY_FILLED.isActive());

        assertFalse(OrderStatus.FILLED.isActive());
        assertFalse(OrderStatus.CANCELLED.isActive());
    }
}
