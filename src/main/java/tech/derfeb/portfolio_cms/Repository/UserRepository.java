package tech.derfeb.portfolio_cms.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.derfeb.portfolio_cms.Model.UserModel;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, String> {
    // Crucial for login logic
    Optional<UserModel> findByUsername(String username);
}