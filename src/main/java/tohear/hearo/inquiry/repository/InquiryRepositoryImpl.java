package tohear.hearo.inquiry.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import static tohear.hearo.inquiry.domain.QInquiry.inquiry;
import static tohear.hearo.user.guardian.QGuardUser.guardUser;
import static tohear.hearo.user.institution.QInstitutionsUser.institutionsUser;
import static tohear.hearo.user.ward.QWardUser.wardUser;

import lombok.RequiredArgsConstructor;
import tohear.hearo.inquiry.dto.response.InquiryResponse;
import tohear.hearo.user.auth.domain.UserType;

@RequiredArgsConstructor
public class InquiryRepositoryImpl implements InquiryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<InquiryResponse> findMine(String userId, UserType userType, Pageable pageable) {
        List<InquiryResponse> inquiries = baseQuery(userType)
                .where(inquiry.userId.eq(userId), inquiry.userType.eq(userType))
                .orderBy(inquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory.select(inquiry.count())
                .from(inquiry)
                .where(inquiry.userId.eq(userId), inquiry.userType.eq(userType))
                .fetchOne();

        return new PageImpl<>(inquiries, pageable, count == null ? 0 : count);
    }

    @Override
    public Optional<InquiryResponse> findMineById(Long inquiryId, String userId, UserType userType) {
        return Optional.ofNullable(baseQuery(userType)
                .where(inquiry.id.eq(inquiryId), inquiry.userId.eq(userId), inquiry.userType.eq(userType))
                .fetchOne());
    }

    private com.querydsl.jpa.impl.JPAQuery<InquiryResponse> baseQuery(UserType userType) {
        StringExpression userName = switch (userType) {
            case WARD -> wardUser.name;
            case GUARDIAN -> guardUser.name;
            case INSTITUTIONS -> institutionsUser.name;
        };

        var query = queryFactory.select(Projections.constructor(
                        InquiryResponse.class,
                        inquiry.id, inquiry.userId, userName, inquiry.userType,
                        inquiry.title, inquiry.content, inquiry.status, inquiry.answer,
                        inquiry.createdAt, inquiry.answeredAt))
                .from(inquiry);

        return switch (userType) {
            case WARD -> query.join(wardUser).on(inquiry.userId.eq(wardUser.id));
            case GUARDIAN -> query.join(guardUser).on(inquiry.userId.eq(guardUser.id));
            case INSTITUTIONS -> query.join(institutionsUser).on(inquiry.userId.eq(institutionsUser.id));
        };
    }
}
