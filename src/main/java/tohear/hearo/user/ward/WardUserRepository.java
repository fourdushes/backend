package tohear.hearo.user.ward;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WardUserRepository extends JpaRepository<WardUser, String> {

    Optional<String> findIdByNameAndEmail(String name, String email);
    boolean existsByEmail(String email);
    Optional<WardUser> findByEmail(String email);

}
