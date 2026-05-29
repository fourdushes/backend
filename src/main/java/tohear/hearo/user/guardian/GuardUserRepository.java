package tohear.hearo.user.guardian;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardUserRepository extends JpaRepository<GuardUser, String> {

    Optional<String> findIdByNameAndEmail(String name, String email);
    boolean existsByEmail(String email);
    Optional<GuardUser> findByEmail(String email);

}
