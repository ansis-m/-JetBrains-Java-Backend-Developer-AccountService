package account.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImp implements UserService{


    private UserRepository userRepository;

    @Autowired
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {

        return userRepository.findAll();

    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findById(email).orElse(null);
    }

    @Override
    public void deleteByEmail(String email) {
        userRepository.deleteById(email);


    }

    @Override
    public boolean exists(String email) {
        return userRepository.existsById(email);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByName(username);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Override
    public long getCount() {
        return userRepository.count();
    }

}
