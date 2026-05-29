package tohear.hearo.user.institution;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionsUserRepository extends JpaRepository<InstitutionsUser, String> {

    Optional<String> findIdByNameAndEmail(String name, String email);
    boolean existsByEmail(String email);
    Optional<InstitutionsUser> findByEmail(String email);

}
