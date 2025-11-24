import java.util.List;

public class LoginService {

	private List<User> users;

	public LoginService(List<User> users) {
		this.users = users;
	}

	public User login() {
		
		loginPage();

		while (true) {

			System.out.print("Username: ");
			String username = ScannerProvider.getConsoleScanner().nextLine();
			System.out.print("Password: ");
			String password = ScannerProvider.getConsoleScanner().nextLine();

			User user = findUser(username, password);
			if(user != null) {
				System.out.println("Welcome " +  user.getUsername() + "!");
				return user;
			}
			else {
				System.out.println("Invalid credentials! Try again.\n");
			}

		}
	}

	private User findUser(String username, String password) {

		for (User user : users) {
			if (user.getUsername().equals(username) && user.getPassword().equals(password))
				return user;
		}

		return null;
	}

	// Display beauty :)
	private static void loginPage() {

		String ROSE = "\u001B[95m";
		String RESET = "\u001B[0m";

		System.out.println(ROSE + " _                 _                              \n"
				+ "| |               (_)                             \n"
				+ "| |     ___   __ _ _ _ __  _ __   __ _  __ _  ___ \n"
				+ "| |    / _ \\ / _` | | '_ \\| '_ \\ / _` |/ _` |/ _ \\\n"
				+ "| |___| (_) | (_| | | | | | |_) | (_| | (_| |  __/\n"
				+ "\\_____/\\___/ \\__, |_|_| |_| .__/ \\__,_|\\__, |\\___|\n"
				+ "              __/ |       | |           __/ |     \n"
				+ "             |___/        |_|          |___/      \n" + RESET);

	}

}
