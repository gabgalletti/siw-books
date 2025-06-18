package uniroma3.it.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.Credentials;
import uniroma3.it.siwbooks.repository.CredentialsRepository;

import java.util.Optional;

@Service
public class CredentialsService {
    @Autowired private CredentialsRepository credentialsRepository;
    public void save(Credentials credentials) {credentialsRepository.save(credentials);}

    public Credentials findByUsername(String username) {return credentialsRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Username not found"));}
}
