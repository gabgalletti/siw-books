package uniroma3.it.siwbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uniroma3.it.siwbooks.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
