package tohear.hearo.care.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import tohear.hearo.care.domain.Care;
import tohear.hearo.care.domain.CareState;
import tohear.hearo.care.domain.QCare;
import tohear.hearo.care.dto.response.GuardSearchDto;
import tohear.hearo.care.dto.response.WardSearchDto;
import tohear.hearo.user.guardian.GuardUser;
import tohear.hearo.user.guardian.QGuardUser;
import tohear.hearo.user.ward.QWardUser;
import tohear.hearo.user.ward.WardUser;

@RequiredArgsConstructor
public class CareRepositoryImpl implements CareRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 피보호자가 보호자를 조회
    @Override
    public List<GuardSearchDto> findGuardUser(WardUser wardUser) {
        return queryFactory
                .select(Projections.constructor(GuardSearchDto.class,
                        QCare.care.id,
                        QGuardUser.guardUser.id,
                        QGuardUser.guardUser.name,
                        QGuardUser.guardUser.userType,
                        QCare.care.mainGuardUser
                ))
                .from(QCare.care)
                .join(QCare.care.guardUser, QGuardUser.guardUser)
                .where(QCare.care.wardUser.eq(wardUser).and(QCare.care.careState.eq(CareState.APPROVED)))
                .fetch();
    }

    // 보호자가 피보호자를 조회
    @Override
    public List<WardSearchDto> findWardUser(GuardUser guardUser) {
        return queryFactory
                .select(Projections.constructor(WardSearchDto.class,
                        QCare.care.id,
                        QWardUser.wardUser.id,
                        QWardUser.wardUser.name,
                        QWardUser.wardUser.userType,
                        QCare.care.mainGuardUser
                ))
                .from(QCare.care)
                .join(QCare.care.wardUser, QWardUser.wardUser)
                .where(QCare.care.guardUser.eq(guardUser).and(QCare.care.careState.eq(CareState.APPROVED)))
                .fetch();
    }

    @Override
    public Page<WardUser> findWardUserToCare(String wardUserId, Pageable pageable) {
        List<WardUser> wardUserList = queryFactory
            .select(QWardUser.wardUser)
            .from(QWardUser.wardUser)
            .where(QWardUser.wardUser.id.contains(wardUserId))
            .orderBy(QWardUser.wardUser.id.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long result = queryFactory
            .select(QWardUser.wardUser.count())
            .from(QWardUser.wardUser)
            .where(QWardUser.wardUser.id.contains(wardUserId))
            .fetchOne();

        long count = result != null ? result : 0L;

        return new PageImpl<>(wardUserList, pageable, count);
                    
    }

    @Override
    public List<Care> findCareByGuardUser(GuardUser guardUser) {
        return queryFactory
                .selectFrom(QCare.care)
                .where(QCare.care.guardUser.eq(guardUser))
                .fetch();
    }

    @Override
    public List<Care> findCareByWardUser(WardUser wardUser) {
        return queryFactory
                .selectFrom(QCare.care)
                .where(QCare.care.wardUser.eq(wardUser))
                .fetch();
    }

    @Override
    public boolean existsActiveCare(GuardUser guardUser, WardUser wardUser) {
        Integer result = queryFactory
                .selectOne()
                .from(QCare.care)
                .where(
                    QCare.care.guardUser.eq(guardUser),
                    QCare.care.wardUser.eq(wardUser),
                    QCare.care.careState.in(
                        CareState.PENDING,
                        CareState.APPROVED
                    )
                )
                .fetchFirst();

        return result != null; // result가 null이면 F(없음), result가 1이면 T(이미 존재)
    }

    @Override
    public List<Care> findByUserId(String id) {

        return queryFactory
            .select(QCare.care)
            .from(QCare.care)
            .join(QCare.care.wardUser, QWardUser.wardUser)
            .where(QWardUser.wardUser.id.eq(id),
                   QCare.care.careState.in(CareState.APPROVED)
                )
            .fetch();
    }

    @Override
    public boolean existMainGuard(WardUser wardUser) {

        return queryFactory
            .selectFrom(QCare.care)
            .where(
                QCare.care.wardUser.id.eq(wardUser.getId()),
                QCare.care.mainGuardUser.eq(true),
                QCare.care.careState.in(CareState.APPROVED)
            )
            .fetchFirst() != null;

    }

    @Override
    public Optional<Care> findMainGuard(WardUser wardUser) {

        return Optional.ofNullable(
            queryFactory
                .selectFrom(QCare.care)
                .where(
                    QCare.care.wardUser.id.eq(wardUser.getId()),
                    QCare.care.mainGuardUser.eq(true),
                    QCare.care.careState.in(CareState.APPROVED)
                )
                .fetchFirst()
        );
    }

    @Override
    public Optional<Care> findChangeMainGuard(WardUser wardUser, GuardUser guardUser) {

        return Optional.ofNullable(
            queryFactory
                .selectFrom(QCare.care)
                .where(
                    QCare.care.wardUser.id.eq(wardUser.getId()),
                    QCare.care.guardUser.id.eq( guardUser.getId()),
                    QCare.care.careState.in(CareState.APPROVED)
                )
                .fetchFirst()
        );
    }
    

}
