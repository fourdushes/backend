package tohear.hearo.care.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import tohear.hearo.care.domain.Care;
import tohear.hearo.care.domain.CareState;
import tohear.hearo.care.domain.QCare;
import tohear.hearo.user.guardian.GuardUser;
import tohear.hearo.user.guardian.QGuardUser;
import tohear.hearo.user.ward.QWardUser;
import tohear.hearo.user.ward.WardUser;

@RequiredArgsConstructor
public class CareRepositoryImpl implements CareRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GuardUser> findGuardUser(WardUser wardUser) {
        return queryFactory
                .select(QGuardUser.guardUser)
                .from(QCare.care)
                .join(QGuardUser.guardUser).fetchJoin()
                .where(QCare.care.wardUser.eq(wardUser).and(QCare.care.careState.eq(CareState.APPROVED)))
                .fetch();
    }

    @Override
    public List<WardUser> findWardUser(GuardUser guardUser) {
        return queryFactory
                .select(QWardUser.wardUser)
                .from(QCare.care)
                .join(QWardUser.wardUser).fetchJoin()
                .where(QCare.care.guardUser.eq(guardUser).and(QCare.care.careState.eq(CareState.APPROVED)))
                .fetch();
    }

    @Override
    public List<WardUser> findWardUserToCare(String wardUserId) {
        return queryFactory
                .select(QWardUser.wardUser)
                .from(QWardUser.wardUser)
                .where(QWardUser.wardUser.id.contains(wardUserId))
                .fetch();
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
    

}
