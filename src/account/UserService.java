package account;

import java.util.List;

public interface UserService {


    List<User> getAll();
    void save(User user);
    User findByEmail(String email);
    void deleteByEmail(String email);
}
