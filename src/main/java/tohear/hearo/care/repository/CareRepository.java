package tohear.hearo.care.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tohear.hearo.care.domain.Care;
import tohear.hearo.user.guardian.GuardUser;
import tohear.hearo.user.ward.WardUser;

public interface CareRepository extends JpaRepository<Care, Long>, CareRepositoryCustom {

    List<WardUser> findWardUser2(GuardUser findGuardUser);

}
