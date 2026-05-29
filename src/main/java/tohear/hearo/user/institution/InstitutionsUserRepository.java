package tohear.hearo.user.institution;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionsUserRepository extends JpaRepository<InstitutionsUser, String> {

    String findIdByNameAndEmail(String name, String email);

}
