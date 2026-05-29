package tohear.hearo.user.ward;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    String findIdByNameAndEmail(String name, String email);

}
