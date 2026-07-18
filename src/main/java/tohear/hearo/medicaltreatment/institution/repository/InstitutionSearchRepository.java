package tohear.hearo.medicaltreatment.institution.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tohear.hearo.user.institution.InstitutionsUser;

public interface InstitutionSearchRepository extends JpaRepository<InstitutionsUser, String> {

    @Query("select iu from InstitutionsUser iu "
            + "where :keyword is null or :keyword = '' "
            + "or lower(iu.id) like lower(concat('%', :keyword, '%')) "
            + "or lower(iu.name) like lower(concat('%', :keyword, '%')) "
            + "or lower(iu.email) like lower(concat('%', :keyword, '%')) "
            + "order by iu.name asc")
    List<InstitutionsUser> search(@Param("keyword") String keyword);
}
