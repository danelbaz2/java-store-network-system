public class VIPCustomer extends Customer {
	public VIPCustomer(String fullName, String id, String phoneNum) {
		super(fullName, id, phoneNum);
	}

	public double getDiscount() // multiply the price by this factor
	{
		return 0.5;
	}

	@Override
	public String toString() {
		return super.toString() + this.getClass().getName();
	}
}
