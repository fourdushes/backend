package tohear.hearo.user.guardian;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuardUserRepository extends JpaRepository<GuardUser, String> {

    @Query("select gu.id from GuardUser gu where gu.name = :name and gu.email = :email")
    Optional<String> findIdByNameAndEmail(@Param("name") String name, @Param("email") String email);
    boolean existsByEmail(String email);
    Optional<GuardUser> findByEmail(String email);

}
