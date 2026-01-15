package com.storemgmt.model.customer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.storemgmt.utils.JsonLoader;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerDiscountTest {

    @BeforeAll
    static void setup() {
        JsonLoader.load("config.json");
    }

    @Test
    void testVIPCustomer50PercentOff() {
        VIPCustomer c = new VIPCustomer("Bob", "C003", "0503333333");
        // VIP pays 50% (0.5 = 50% off)
        assertEquals(0.5, c.getDiscount());
    }

    @Test
    void testCustomerHasShoppingList() {
        NewCustomer c = new NewCustomer("John", "C001", "0501234567");
        assertNotNull(c.getShoppingList());
        assertFalse(c.getShoppingList().isEmpty());
    }
}
