import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

//This class is loading and storing all what is needed for users in the system,
//It's like an internal database, gathering data from a file, called in the main

public class FilerUserRepository implements UserRepository {

	private final String path;
	private List<User> users = new ArrayList<User>();

	public FilerUserRepository(String path) {
		this.path = path;
	}

	@Override
	public List<User> loadUsers() throws FileNotFoundException{
		users.clear(); //If reloading, remove and reload
		
		Scanner sc = ScannerProvider.getFileScanner(new File(path));	

		while (sc.hasNextLine()) {
			String data[] = sc.nextLine().split("\t");
			
			String username = data[0];
			String password = data[1];
			Role role = Enum.valueOf(Role.class, data[2]);
			
			users.add(new User(username, password, role));
		}

		return Collections.unmodifiableList(users); //secure unchangeable list
	}

	@Override
	public List<User> getUsers() {
		return Collections.unmodifiableList(users);
	}

	@Override
	public boolean addUsers(User user) {
		if(user != null)
			return users.add(user);
		
		return false;
	}

}
