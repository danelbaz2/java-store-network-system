public class NewCustomer extends Customer
{
    public NewCustomer(String fullName, String id, String phoneNum)
    {
        super(fullName, id, phoneNum);
    }

    public double getDiscount() //multiply the price by this factor
    {
        return 1;
    }
    
    @Override
    public String toString() {
    	return super.toString() + this.getClass().getName();
    }
}
