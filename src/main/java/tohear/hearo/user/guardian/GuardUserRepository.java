package tohear.hearo.user.guardian;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardUserRepository extends JpaRepository<GuardUser, String> {

    String findIdByNameAndEmail(String name, String email);

}
