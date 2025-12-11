import java.util.HashMap;

public class ReturningCustomer extends Customer
{
    public ReturningCustomer(String fullName, String id, String phoneNum)
    {
        super(fullName, id, phoneNum);
    }

    public double getDiscount() //multiply the price by this factor
    {
        return 0.8;
    }
    
    @Override
    public String toString() {	
    	return super.toString() + this.getClass().getName();
    }

	@Override
	public HashMap<Product, Integer> startBuying() {
		return null;
	}
}
