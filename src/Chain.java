import java.util.LinkedList;

public class Chain {

	private String name;
	private LinkedList<Branch> branches;
	private LinkedList<Customer> customers;

	public Chain(String name) {
		this.name = name;
		this.branches = new LinkedList<Branch>();
		this.customers = new LinkedList<Customer>();
	}

	public LinkedList<Customer> getCustomers() {
		return customers;
	}

	public void addCustomer(Customer customer) {
		customers.add(customer);
	}

	public Customer getCustomer(String id) {
		for (Customer c : customers) {
			if (c.getId().equals(id))
				return c;
		}

		System.out.println("Customer with ID: " + id + "does not exist in the chain " + this.name);
		return null;
	}

	public LinkedList<Branch> getBranches() {
		return branches;
	}

}
