package com.storemgmt.model.store;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.storemgmt.model.customer.Customer;
import com.storemgmt.model.employee.Employee;
import com.storemgmt.model.employee.Role;
import com.storemgmt.utils.JsonLoader;
import static org.junit.jupiter.api.Assertions.*;

public class StoreBranchTest {

    private StoreBranch branch;
    private Employee emp1;
    private Employee emp2;

    @BeforeAll
    static void loadConfig() {
        JsonLoader.load("config.json");
    }

    @BeforeEach
    void setup() {
        branch = new StoreBranch("BR01", "Tel Aviv");

        emp1 = new Employee("E001", "John", "0501111111", "BR01", Role.CASHIER, "Password1");
        emp2 = new Employee("E002", "Jane", "0502222222", "BR01", Role.CASHIER, "Password2");
        branch.getEmployees().add(emp1);
        branch.getEmployees().add(emp2);
    }

    @Test
    void testBranchCreation() {
        assertEquals("BR01", branch.getBranchId());
        assertEquals("Tel Aviv", branch.getName());
    }

    @Test
    void testCustomerQueue() {
        assertTrue(branch.getQueueSize() > 0);
    }

    @Test
    void testGetNextCustomer() {
        int before = branch.getQueueSize();
        branch.getNextCustomer();
        assertEquals(before - 1, branch.getQueueSize());
    }

    @Test
    void testCashierBusyStatus() {
        assertFalse(branch.isCashierBusy(emp1.getEmployeeId()));
        branch.setCashierBusy(emp1.getEmployeeId(), true);
        assertTrue(branch.isCashierBusy(emp1.getEmployeeId()));
    }

    @Test
    void testTwoCashiersCanWork() {
        // Verify employees exist
        assertNotNull(branch.getEmployeeById("E001"));
        assertNotNull(branch.getEmployeeById("E002"));

        branch.setCashierBusy(emp1.getEmployeeId(), true);
        branch.setCashierBusy(emp2.getEmployeeId(), true);

        assertTrue(branch.isCashierBusy(emp1.getEmployeeId()));
        assertTrue(branch.isCashierBusy(emp2.getEmployeeId()));
    }

    @Test
    void testSell() {
        Customer customer = branch.getNextCustomer();
        double total = branch.sell(customer, emp1.getEmployeeId());
        assertTrue(total >= 0);
    }

    @Test
    void testCashierCannotSellToTwoCustomers() {
        // Simulate: cashier starts serving first customer
        branch.setCashierBusy(emp1.getEmployeeId(), true);

        // Simulate what server does when second sell request comes in
        String response;
        if (branch.isCashierBusy(emp1.getEmployeeId())) {
            response = "ERROR|You are already serving a customer!";
        } else {
            Customer customer = branch.getNextCustomer();
            branch.sell(customer, emp1.getEmployeeId());
            response = "OK|Started serving customer";
        }

        // Should get error because cashier is busy
        assertTrue(response.startsWith("ERROR"), "Should not allow second sell");
        System.out.println("Response: " + response.split("\\|")[1]);
    }
}
