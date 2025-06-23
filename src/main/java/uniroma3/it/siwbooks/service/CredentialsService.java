package uniroma3.it.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.Credentials;
import uniroma3.it.siwbooks.repository.CredentialsRepository;

import java.util.Optional;

@Service
public class CredentialsService {
    @Autowired private CredentialsRepository credentialsRepository;
    public void save(Credentials credentials) {credentialsRepository.save(credentials);}

    public Credentials findByUsername(String username) {return credentialsRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Username not found"));}

    public Credentials getLoggedCredentials() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        return findByUsername(currentUsername);
    }
}
