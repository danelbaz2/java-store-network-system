public class User {

	private String username;
	private String password;
	private final Role role;

	public User(String username, String password, Role role) {

		this.username = username;
		this.password = password;
		this.role = role;
		
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	@Override
	public String toString() {
		return getUsername() + " - " + getPassword() + " - " + getRole();
	}
	
	public boolean isAdmin() {
		return role == Role.ADMIN;
	}
	
	public Role getRole() {
		return role;
	}
}
