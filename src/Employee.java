public class Employee {
	private String firstName;
	private String lastName;
	private String phoneNum;
	private String accountNum;
	private String branch;
	private String id;
	private Role role;

	public void Employee(String firstName, String lastName, String phoneNum, String accountNum, String id, Role role) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNum = phoneNum;
		this.accountNum = accountNum;
		this.id = id;
		this.role = role;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public String getAccountNum() {
		return accountNum;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getID() {
		return id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("%s\n %s\n %s\n %s\n %s\n %s\n", this.firstName, this.lastName, this.id, this.phoneNum,
				this.accountNum, this.branch, this.accountNum, this.role);
	}
}
