package com.storemgmt.store;

import com.storemgmt.model.employee.Employee;
import com.storemgmt.model.employee.Role;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EmployeeStore {

    private final Map<String, Employee> byId = new HashMap<>();
    private final String csvPath;

    public EmployeeStore(String csvPath) {
        this.csvPath = csvPath;
        loadFromCsv();
    }

    public void add(Employee employee) {
        byId.put(employee.getEmployeeId(), employee);
        saveToCsv();
    }

    public Employee findById(String employeeId) {
        return byId.get(employeeId);
    }

    public boolean exists(String employeeId) {
        return byId.containsKey(employeeId);
    }

    public Collection<Employee> getAll() {
        return byId.values();
    }

    public void update(Employee employee) {
        byId.put(employee.getEmployeeId(), employee);
        saveToCsv();
    }

    public void delete(String employeeId) {
        byId.remove(employeeId);
        saveToCsv();
    }

    private void loadFromCsv() {
        byId.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) { // skip header
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty())
                    continue;

                // CSV format:
                // employeeId,fullName,phone,branchId,role,password
                String[] parts = line.split(",", -1);
                if (parts.length < 6)
                    continue;

                String employeeId = parts[0].trim();
                String fullName = parts[1].trim();
                String phone = parts[2].trim();
                String branchId = parts[3].trim();
                Role role = Role.valueOf(parts[4].trim());
                String password = parts[5];

                Employee e = new Employee(employeeId, fullName, phone, branchId, role, password);
                byId.put(employeeId, e);
            }
        } catch (Exception ex) {
            // If file doesn't exist yet or can't be read, we start with empty store
            // (Later we can show a message / create the file automatically)
        }
    }

    private void saveToCsv() {
        try (PrintWriter out = new PrintWriter(new FileWriter(csvPath, false))) {
            out.println("employeeId,fullName,phone,branchId,role,password");
            for (Employee e : byId.values()) {
                out.println(
                        e.getEmployeeId() + ","
                                + escape(e.getFullName()) + ","
                                + escape(e.getPhone()) + ","
                                + escape(e.getBranchId()) + ","
                                + e.getRole().name() + ","
                                + escape(e.getPassword()));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save employees CSV: " + ex.getMessage(), ex);
        }
    }

    // Minimal escape to avoid breaking CSV when user types commas
    private String escape(String value) {
        if (value == null)
            return "";
        return value.replace(",", " ");
    }
}
