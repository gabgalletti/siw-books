package uniroma3.it.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.Credentials;
import uniroma3.it.siwbooks.model.User;
import uniroma3.it.siwbooks.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CredentialsService credentialsService;

    public void save(User user) {userRepository.save(user);}

    public User getLoggedUser() {
        Credentials credentials = credentialsService.getLoggedCredentials();
        return credentials.getUser();
    }

}
