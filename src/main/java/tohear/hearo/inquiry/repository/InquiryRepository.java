package tohear.hearo.inquiry.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tohear.hearo.inquiry.domain.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquiryRepositoryCustom {
}
