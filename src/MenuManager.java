
public class MenuManager {

	public void showMenuFor(User user) {
		if(user.isAdmin())
			showAdminMenu();
		else
			showRegularMenu();

	}
	
	private void showAdminMenu() {
		System.out.println("=== ADMIN MENU ===");
	}
	
	private void showRegularMenu() {
		System.out.println("=== REGULAR MENU ===");
	}



}
