package com.storemgmt.model.store;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.storemgmt.model.customer.Customer;
import com.storemgmt.model.employee.Employee;
import com.storemgmt.model.product.*;
import com.storemgmt.utils.JsonLoader;
import com.google.gson.JsonParser;

public class StoreBranch {
    private String branchId;
    private String name;
    private Map<Product, Integer> products;
    private LinkedList<Employee> employees;
    private Map<Product, Integer> sellRecord;

    public StoreBranch(String branchId, String name) {
        this.branchId = branchId;
        this.name = name;
        products = new HashMap<Product, Integer>();
        employees = new LinkedList<Employee>();
        sellRecord = new HashMap<Product, Integer>();
        initializeInventory();
    }

    private void initializeInventory() {
        this.products.put(new Jean("P001", BrandType.ADIDAS), 50);
        this.products.put(new Jean("P002", BrandType.BERSHKA), 50);
        this.products.put(new Jean("P003", BrandType.LEVIS), 50);
        this.products.put(new Jean("P004", BrandType.NIKE), 50);

        this.products.put(new LongShirt("P005", BrandType.BERSHKA), 50);
        this.products.put(new LongShirt("P006", BrandType.LEVIS), 50);
        this.products.put(new LongShirt("P007", BrandType.POLO), 50);

        this.products.put(new Bra("P008", BrandType.NIKE), 50);
        this.products.put(new Bra("P009", BrandType.ADIDAS), 50);
        this.products.put(new Bra("P010", BrandType.BERSHKA), 50);

        this.products.put(new RunningShoe("P011", BrandType.NIKE), 50);
        this.products.put(new RunningShoe("P012", BrandType.ADIDAS), 50);
    }

    public String getBranchId() {
        return branchId;
    }

    public String getName() {
        return name;
    }

    public LinkedList<Employee> getEmployees() {
        return employees;
    }

    public Employee getEmployeeById(String empId) {
        for (Employee emp : employees) {
            if (emp.getEmployeeId().equals(empId)) {
                return emp;
            }
        }
        return null;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        // Parse the JSON string from toString() and add as a proper JSON object
        JsonLoader.add("employees." + employee.getEmployeeId(),
                JsonParser.parseString(employee.toString()));
        JsonLoader.save();
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        JsonLoader.remove("employees." + employee.getEmployeeId());
        JsonLoader.save();
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public Product getProductById(String productId) {
        for (Product product : products.keySet()) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    public Map<Product, Integer> getSellRecord() {
        return sellRecord;
    }

    public synchronized void sell(Customer customer) {
        double sum = 0;
        Map<Product, Integer> shoppingList = customer.getShoppingList();

        for (Map.Entry<Product, Integer> product : shoppingList.entrySet()) {
            if (!products.containsKey(product.getKey())) {
                System.out.println("Product '" + product.getKey().getClass().getSimpleName() + " "
                        + product.getKey().getBrand() + "' does not exist in branch " + this.name);
                continue;
            }

            int currentAmount = products.get(product.getKey()).intValue();
            int amountToBuy = product.getValue();

            if (currentAmount < amountToBuy) {
                System.out.println("Product '" + product.getKey().getClass().getSimpleName()
                        + "' not available in sufficient quantity in branch " + this.name);
                continue;
            }

            if (!sellRecord.containsKey(product.getKey())) {
                sellRecord.put(product.getKey(), 0);
            }

            int soldProduct = sellRecord.get(product.getKey()).intValue();
            sellRecord.put(product.getKey(), soldProduct + amountToBuy);

            products.put(product.getKey(), currentAmount - amountToBuy);

            sum += product.getKey().getPrice() * amountToBuy;
        }

        double finalPrice = sum * customer.getDiscount();
        System.out.println("Customer " + customer.getFullName() + " gets the " + customer.getClass().getSimpleName()
                + " discount.");
        System.out.println("Original price: $" + String.format("%.2f", sum));
        System.out.println("Final price: $" + String.format("%.2f", finalPrice));
    }

    public void displayProducts() {
        System.out.println("=== Products in " + this.name + " ===");
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            if (entry.getValue() > 0)
                System.out.println("Product: '" + entry.getKey() + "' - Quantity: " + entry.getValue());
        }
    }

    public void updateProductQuantity(Product product, int quantity) {
        products.put(product, quantity);
    }
}
