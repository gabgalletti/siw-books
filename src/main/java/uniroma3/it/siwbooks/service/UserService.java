package uniroma3.it.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.User;
import uniroma3.it.siwbooks.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    public void save(User user) {userRepository.save(user);}

}
