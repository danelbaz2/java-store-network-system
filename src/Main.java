import java.io.FileNotFoundException;
import java.util.List;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		
		UserRepository userRepo = new FilerUserRepository("users.txt");
		List<User> users = userRepo.loadUsers();	
		
		LoginService LoginService = new LoginService(users);
		User loggedInUser = LoginService.login();
		
		MenuManager menuManager = new MenuManager();
		menuManager.showMenuFor(loggedInUser);
	}

}
