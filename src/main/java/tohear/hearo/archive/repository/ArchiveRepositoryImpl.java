package tohear.hearo.archive.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import tohear.hearo.archive.domain.Archive;
import tohear.hearo.archive.domain.QArchive;
import tohear.hearo.user.ward.QWardUser;

@RequiredArgsConstructor
public class ArchiveRepositoryImpl implements ArchiveRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Archive> findAllByUserId(String userId, Pageable pageable) {
        
        List<Archive> archiveList = queryFactory
            .selectFrom(QArchive.archive)
            .join(QArchive.archive.wardUser, QWardUser.wardUser)
            .where(QWardUser.wardUser.name.eq(userId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long result = queryFactory
            .select(QArchive.archive.count())
            .from(QArchive.archive)
            .join(QArchive.archive.wardUser, QWardUser.wardUser)
            .where(QWardUser.wardUser.name.eq(userId))
            .fetchOne();

        long count = result != null ? result : 0L;

        return new PageImpl<>(archiveList, pageable, count);

    }

}
