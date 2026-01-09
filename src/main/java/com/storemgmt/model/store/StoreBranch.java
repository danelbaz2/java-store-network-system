package com.storemgmt.model.store;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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

    public StoreBranch(String branchId, String name) {
        this.branchId = branchId;
        this.name = name;
        products = new HashMap<Product, Integer>();
        employees = new LinkedList<Employee>();
        sellRecord = new HashMap<Product, Integer>();
        customerQueue = new LinkedList<Customer>();
        initializeInventory();
        generateCustomers(10);
    }

    // Generate random customers and add to queue
    private void generateCustomers(int count) {
        String[] names = { "John", "Sarah", "Mike", "Emma", "David", "Lisa", "Tom", "Anna", "James", "Maria" };

        for (int i = 0; i < count; i++) {
            String name = names[(int) (Math.random() * names.length)];
            String id = "C" + (1000 + i);
            String phone = "050" + (1000000 + i);

            // Randomly pick a customer type
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

    // Get next customer from queue (or null if empty)
    public Customer getNextCustomer() {
        return customerQueue.poll();
    }

    // Check how many customers are waiting
    public int getQueueSize() {
        return customerQueue.size();
    }

    private void initializeInventory() {
        // Load products from JSON config
        for (String productId : JsonLoader.getKeys("products")) {
            String type = JsonLoader.getString("products." + productId + ".type");
            String brand = JsonLoader.getString("products." + productId + ".brand");
            double price = JsonLoader.getDouble("products." + productId + ".price");
            int stock = JsonLoader.getInt("products." + productId + ".stock");

            BrandType brandType = BrandType.valueOf(brand);
            Product product = createProduct(productId, type, brandType, price);

            if (product != null) {
                this.products.put(product, stock);
            }
        }
        System.out.println("  Loaded " + products.size() + " products for branch " + branchId);
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

    public String getReportByBranch() {
        String message;

        if (sellRecord.size() == 0)
            message = "No record";
        else
            message = sellRecord.toString();

        return "Branch: " + name + "|"
                + "Sell Record:|" + message;
    }

    public String getReportByCategory() {
        return "";

    }

    public String getReportByProduct() {
        return "";
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

    public synchronized double sell(Customer customer) {
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

        return finalPrice;
    }

    public void displayProducts() {
        System.out.println("=== Products in " + this.name + " ===");
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            if (entry.getValue() > 0)
                System.out.println("Product: '" + entry.getKey() + "' - Quantity: " + entry.getValue());
        }
    }
}
