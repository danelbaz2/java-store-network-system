package com.storemgmt.model.store;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.storemgmt.model.customer.Customer;
import com.storemgmt.model.customer.NewCustomer;
import com.storemgmt.model.customer.ReturningCustomer;
import com.storemgmt.model.customer.VIPCustomer;
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
    private Queue<Customer> customerQueue;
    private static final int MAX_CASHIERS = 3;
    private ExecutorService cashierPool;
    private Map<String, Boolean> cashierBusyStatus;
    private Map<String, Double> cashierSales;

    public StoreBranch(String branchId, String name) {
        this.branchId = branchId;
        this.name = name;
        products = new HashMap<Product, Integer>();
        employees = new LinkedList<Employee>();
        sellRecord = new HashMap<Product, Integer>();
        customerQueue = new LinkedList<Customer>();
        cashierPool = Executors.newFixedThreadPool(MAX_CASHIERS);
        cashierBusyStatus = new HashMap<String, Boolean>();
        cashierSales = new HashMap<String, Double>();
        initializeInventory();
        generateCustomers(10);
    }

    private void generateCustomers(int count) {
        String[] names = { "John", "Sarah", "Mike", "Emma", "David", "Lisa", "Tom", "Anna", "James", "Maria" };
        for (int i = 0; i < count; i++) {
            String name = names[(int) (Math.random() * names.length)];
            String id = "C" + (1000 + i);
            String phone = "050" + (1000000 + i);
            int type = (int) (Math.random() * 3);
            Customer customer;
            if (type == 0) {
                customer = new NewCustomer(name, id, phone);
            } else if (type == 1) {
                customer = new ReturningCustomer(name, id, phone);
            } else {
                customer = new VIPCustomer(name, id, phone);
            }
            customerQueue.add(customer);
        }
        System.out.println("  Generated " + count + " customers for branch " + branchId);
    }

    public Customer getNextCustomer() {
        return customerQueue.poll();
    }

    public int getQueueSize() {
        return customerQueue.size();
    }

    private void initializeInventory() {
        String inventoryPath = "branches." + branchId + ".inventory";
        try {
            for (String productId : JsonLoader.getKeys(inventoryPath)) {
                int stock = JsonLoader.getInt(inventoryPath + "." + productId + ".stock");
                String catalogPath = "productCatalog." + productId;
                String type = JsonLoader.getString(catalogPath + ".type");
                String brand = JsonLoader.getString(catalogPath + ".brand");
                double price = JsonLoader.getDouble(catalogPath + ".price");
                BrandType brandType = BrandType.valueOf(brand);
                Product product = createProduct(productId, type, brandType, price);
                if (product != null) {
                    this.products.put(product, stock);
                }
            }
            System.out.println("  Loaded " + products.size() + " products for branch " + branchId);
        } catch (Exception e) {
            System.err.println("Error loading inventory for branch " + branchId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Product createProduct(String productId, String type, BrandType brand, double price) {
        switch (type) {
            case "Jean":
                return new Jean(productId, brand, price);
            case "Shorts":
                return new Shorts(productId, brand, price);
            case "LongShirt":
                return new LongShirt(productId, brand, price);
            case "TShirt":
                return new TShirt(productId, brand, price);
            case "Bra":
                return new Bra(productId, brand, price);
            case "Boxer":
                return new Boxer(productId, brand, price);
            case "RunningShoe":
                return new RunningShoe(productId, brand, price);
            case "Sneaker":
                return new Sneaker(productId, brand, price);
            default:
                System.out.println("Unknown product type: " + type);
                return null;
        }
    }

    public String getReportByBranch(FileWriter writer) {
        if (sellRecord.isEmpty()) {
            saveReportToJson(new HashMap<>(), writer);
            return "BRANCH REPORT: " + name + "|No sales yet.";
        }
        int totalItems = 0;
        double totalRevenue = 0;
        for (Product p : sellRecord.keySet()) {
            totalItems += sellRecord.get(p);
        }
        for (Double sales : cashierSales.values()) {
            totalRevenue += sales;
        }
        String result = "BRANCH REPORT: " + name;
        result += "|Total Sold: " + totalItems + " items";
        result += "|Revenue: $" + String.format("%.2f", totalRevenue);
        result += "|Cashier Sales:";
        for (String cashierId : cashierSales.keySet()) {
            result += "|" + cashierId + ": $" + String.format("%.2f", cashierSales.get(cashierId));
        }
        Map<Object, Object> salesMap = new HashMap<>();
        salesMap.put("totalItems", (double) totalItems);
        salesMap.put("totalRevenue", totalRevenue);
        saveReportToJson(salesMap, writer);
        return result;
    }

    public String getReportByCategory(FileWriter writer) {
        if (sellRecord.isEmpty()) {
            saveReportToJson(new HashMap<>(), writer);
            return "CATEGORY REPORT: " + name + "|No sales yet.";
        }
        Map<ProductType, Integer> categories = new HashMap<>();
        for (Product p : sellRecord.keySet()) {
            ProductType category = p.getProductType();
            int qty = sellRecord.get(p);
            categories.put(category, categories.getOrDefault(category, 0) + qty);
        }
        String result = "CATEGORY REPORT: " + name;
        for (ProductType category : categories.keySet()) {
            result += "|" + category + ": " + categories.get(category) + " sold";
        }
        Map<Object, Object> categoriesMap = new HashMap<>();
        categoriesMap.putAll(categories);
        saveReportToJson(categoriesMap, writer);
        return result;
    }

    public String getReportByProduct(FileWriter writer) {
        if (sellRecord.isEmpty()) {
            saveReportToJson(new HashMap<>(), writer);
            return "PRODUCT REPORT: " + name + "|No sales yet.";
        }
        String result = "PRODUCT REPORT: " + name;
        Map<Object, Object> productsMap = new HashMap<>();
        for (Product p : sellRecord.keySet()) {
            int qty = sellRecord.get(p);
            productsMap.put(p.getClass().getSimpleName(), qty);
            result += "|" + p.getClass().getSimpleName() + " (" + p.getBrand() + "): " + qty + " sold";
        }
        saveReportToJson(productsMap, writer);
        return result;
    }

    public void saveReportToJson(Map<Object, Object> salesMap, FileWriter writer) {
        try {
            writer.append("  {\n");
            writer.append("    \"Branch Name\": \"" + name + "\"");
            for (Object key : salesMap.keySet())
                writer.append(",\n" + "    \"" + key + "\": " + salesMap.get(key));
            writer.append("\n  }");
        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
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
            if (emp.getEmployeeId().equals(empId))
                return emp;
        }
        return null;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        JsonLoader.add("employees." + employee.getEmployeeId(), JsonParser.parseString(employee.toString()));
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
            if (product.getProductId().equals(productId))
                return product;
        }
        return null;
    }

    public Map<Product, Integer> getSellRecord() {
        return sellRecord;
    }

    public synchronized double sell(Customer customer, String cashierId) {
        System.out.println("---------------- START SELLING ----------------");
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
            JsonLoader.set("branches." + branchId + ".inventory." + product.getKey().getProductId() + ".stock",
                    currentAmount - amountToBuy);
            int soldProduct = sellRecord.get(product.getKey()).intValue();
            sellRecord.put(product.getKey(), soldProduct + amountToBuy);
            products.put(product.getKey(), currentAmount - amountToBuy);
            sum += product.getKey().getPrice() * amountToBuy;
        }
        JsonLoader.save();
        double finalPrice = sum * customer.getDiscount();
        double currentCashierTotal = cashierSales.getOrDefault(cashierId, 0.0);
        cashierSales.put(cashierId, currentCashierTotal + finalPrice);
        System.out.println("Customer " + customer.getFullName() + " gets the " + customer.getClass().getSimpleName()
                + " discount.");
        System.out.println("Original price: $" + String.format("%.2f", sum));
        System.out.println("Final price: $" + String.format("%.2f", finalPrice));
        System.out.println("---------------- END SELLING ----------------");
        return finalPrice;
    }

    public void displayProducts() {
        System.out.println("=== Products in " + this.name + " ===");
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            if (entry.getValue() > 0)
                System.out.println("Product: '" + entry.getKey() + "' - Quantity: " + entry.getValue());
        }
    }

    public boolean isCashierBusy(String employeeId) {
        return cashierBusyStatus.getOrDefault(employeeId, false);
    }

    public void setCashierBusy(String employeeId, boolean busy) {
        cashierBusyStatus.put(employeeId, busy);
    }

    public void submitCustomerForCheckout(Runnable customerHandler) {
        cashierPool.execute(customerHandler);
    }

    public void shutdown() {
        System.out.println("Sorry, we are closing down for the day. Please come back tomorrow.");
        cashierPool.shutdown();
        try {
            cashierPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
