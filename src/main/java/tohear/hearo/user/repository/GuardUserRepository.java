package tohear.hearo.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tohear.hearo.user.domain.GuardUser;

public interface GuardUserRepository extends JpaRepository<GuardUser, String> {

}
