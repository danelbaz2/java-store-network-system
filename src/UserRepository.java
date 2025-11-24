import java.io.FileNotFoundException;
import java.util.List;

public interface UserRepository {
	
    List<User> loadUsers() throws FileNotFoundException;
    List<User> getUsers();
    boolean addUsers(User user);
}
