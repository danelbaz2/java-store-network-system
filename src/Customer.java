
public abstract class Customer {
    private String fullName;
    private String id;
    private String phoneNum;
    public abstract double getDiscount();

    public Customer(String fullName, String id, String phoneNum)
    {
        this.fullName = fullName;
        this.id = id;
        this.phoneNum = phoneNum;
    }

	public String getFullName() {
		return fullName;
	}

	public String getId() {
		return id;
	}

	public String getPhoneNum() {
		return phoneNum;
	}
	
	@Override
	public String toString() {
		
		return this.fullName + "\n" + this.id + "\n" + this.phoneNum + "\n";
	}
   
}
