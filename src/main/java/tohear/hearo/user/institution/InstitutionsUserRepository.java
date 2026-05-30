package tohear.hearo.user.institution;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstitutionsUserRepository extends JpaRepository<InstitutionsUser, String> {

    @Query("select iu.id from InstitutionsUser iu where iu.name = :name and iu.email = :email")
    Optional<String> findIdByNameAndEmail(@Param("name") String name, @Param("email") String email);
    boolean existsByEmail(String email);
    Optional<InstitutionsUser> findByEmail(String email);

}
