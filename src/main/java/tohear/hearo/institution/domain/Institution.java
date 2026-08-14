package tohear.hearo.institution.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;

@Entity
@Getter
public class Institution {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Institution_id")
    private Long id;
    private String institutionName;
    private String email;
    private String institutionLoginId;
    private String passward;
    private InstitutionApprovalState institutionState;

    public Institution() {
    }

    public Institution(String institutionName, String email,String institutionLoginId, String passward) {
        this.institutionName = institutionName;
        this.email = email;
        this.institutionLoginId = institutionLoginId;
        this.passward = passward;
        this.institutionState = InstitutionApprovalState.PENDING;
    }

    public void approve() {
        this.institutionState = InstitutionApprovalState.APPROVED;
    }

    public void reject() {
        this.institutionState = InstitutionApprovalState.REJECTED;
    }


}
