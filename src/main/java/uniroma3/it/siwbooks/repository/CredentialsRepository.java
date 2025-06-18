package uniroma3.it.siwbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uniroma3.it.siwbooks.model.Credentials;

import java.util.Optional;


public interface CredentialsRepository extends JpaRepository<Credentials, String> {
    Optional<Credentials> findByUsername(String username);
    Optional<Credentials> findById(Long id);
}
