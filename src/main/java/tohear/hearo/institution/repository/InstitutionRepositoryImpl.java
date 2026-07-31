package tohear.hearo.institution.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import tohear.hearo.institution.domain.QInstitution;
import tohear.hearo.institution.dto.response.JudgeUserDto;
import tohear.hearo.user.institution.InstitutionState;
import tohear.hearo.user.institution.QInstitutionsUser;

@RequiredArgsConstructor
public class InstitutionRepositoryImpl implements InstitutionRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    // 승인대기중인 사용자 보기
    @Override
    public Page<JudgeUserDto> searchPending(Long institutionId, Pageable pageable) {

        List<JudgeUserDto> list = queryFactory
            .select(Projections.constructor(JudgeUserDto.class,
                QInstitutionsUser.institutionsUser.id,
                QInstitutionsUser.institutionsUser.name,
                QInstitutionsUser.institutionsUser.email,
                QInstitutionsUser.institutionsUser.institutionState,
                QInstitution.institution.institutionName
            ))
            .from(QInstitutionsUser.institutionsUser)
            .join(QInstitutionsUser.institutionsUser.institution, QInstitution.institution)
            .where(
                QInstitutionsUser.institutionsUser.institutionState.eq(InstitutionState.PENDING),
                QInstitution.institution.id.eq(institutionId)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long result = queryFactory
            .select(QInstitutionsUser.institutionsUser.count())
            .from(QInstitutionsUser.institutionsUser)
            .join(QInstitutionsUser.institutionsUser.institution, QInstitution.institution)
            .where(
                QInstitutionsUser.institutionsUser.institutionState.eq(InstitutionState.PENDING),
                QInstitution.institution.id.eq(institutionId)
            )
            .fetchOne();

        long count = result != null ? result : 0L;

        return new PageImpl<>(list, pageable, count);
    }

    // 승인대기중인 사용자 보기
    @Override
    public Page<JudgeUserDto> searchApproved(Long institutionId, Pageable pageable) {

        List<JudgeUserDto> list = queryFactory
            .select(Projections.constructor(JudgeUserDto.class,
                QInstitutionsUser.institutionsUser.id,
                QInstitutionsUser.institutionsUser.name,
                QInstitutionsUser.institutionsUser.email,
                QInstitutionsUser.institutionsUser.institutionState,
                QInstitution.institution.institutionName
            ))
            .from(QInstitutionsUser.institutionsUser)
            .join(QInstitutionsUser.institutionsUser.institution, QInstitution.institution)
            .where(
                QInstitutionsUser.institutionsUser.institutionState.eq(InstitutionState.APPROVED),
                QInstitution.institution.id.eq(institutionId)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long result = queryFactory
            .select(QInstitutionsUser.institutionsUser.count())
            .from(QInstitutionsUser.institutionsUser)
            .join(QInstitutionsUser.institutionsUser.institution, QInstitution.institution)
            .where(
                QInstitutionsUser.institutionsUser.institutionState.eq(InstitutionState.APPROVED),
                QInstitution.institution.id.eq(institutionId)
            )
            .fetchOne();

        long count = result != null ? result : 0L;

        return new PageImpl<>(list, pageable, count);
    }

    // 승인대기중인 사용자 보기
    @Override
    public Page<JudgeUserDto> searchReject(Long institutionId, Pageable pageable) {

        List<JudgeUserDto> list = queryFactory
            .select(Projections.constructor(JudgeUserDto.class,
                QInstitutionsUser.institutionsUser.id,
                QInstitutionsUser.institutionsUser.name,
                QInstitutionsUser.institutionsUser.email,
                QInstitutionsUser.institutionsUser.institutionState,
                QInstitution.institution.institutionName
            ))
            .from(QInstitutionsUser.institutionsUser)
            .join(QInstitutionsUser.institutionsUser.institution, QInstitution.institution)
            .where(
                QInstitutionsUser.institutionsUser.institutionState.eq(InstitutionState.REJECTED),
                QInstitution.institution.id.eq(institutionId)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long result = queryFactory
            .select(QInstitutionsUser.institutionsUser.count())
            .from(QInstitutionsUser.institutionsUser)
            .join(QInstitutionsUser.institutionsUser.institution, QInstitution.institution)
            .where(
                QInstitutionsUser.institutionsUser.institutionState.eq(InstitutionState.REJECTED),
                QInstitution.institution.id.eq(institutionId)
            )
            .fetchOne();

        long count = result != null ? result : 0L;

        return new PageImpl<>(list, pageable, count);
    }


}
