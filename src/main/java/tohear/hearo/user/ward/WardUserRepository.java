package tohear.hearo.user.ward;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WardUserRepository extends JpaRepository<WardUser, String> {

    @Query("select w.id from WardUser w where w.name = :name and w.email = :email")
    Optional<String> findIdByNameAndEmail(@Param("name") String name, @Param("email") String email);
    boolean existsByEmail(String email);
    Optional<WardUser> findByEmail(String email);

}
