package com.storemgmt.protocol;

/**
 * Enum defining all available actions that clients can request from the server.
 * 
 * Each action represents a specific operation:
 * - LOGIN: Authenticate a user with employee ID and password
 * - LOGOUT: End the current session
 * - GET_INVENTORY: Retrieve product list for a branch
 * - SELL_PRODUCT: Process a sale (decrease stock)
 * - GET_EMPLOYEES: List all employees (ADMIN only)
 * - ADD_EMPLOYEE: Create new employee (ADMIN only)
 * - UPDATE_EMPLOYEE: Modify employee details (ADMIN only)
 * - DELETE_EMPLOYEE: Remove employee (ADMIN only)
 * - SEND_MESSAGE: Send chat message to other branches
 */
public enum ActionType {
    // Authentication actions
    LOGIN,
    LOGOUT,
    
    // Inventory actions
    GET_INVENTORY,
    SELL_PRODUCT,
    
    // Employee management actions (ADMIN only)
    GET_EMPLOYEES,
    ADD_EMPLOYEE,
    UPDATE_EMPLOYEE,
    DELETE_EMPLOYEE,
    
    // Chat actions
    SEND_MESSAGE,
    GET_MESSAGES
}
