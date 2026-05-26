package tohear.hearo.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tohear.hearo.user.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

    String findIdByNameAndEmail(String name, String email);

}
