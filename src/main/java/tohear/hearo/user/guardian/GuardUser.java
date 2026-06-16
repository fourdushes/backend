package tohear.hearo.user.guardian;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Getter;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.ward.WardUser;

@Entity
@Getter
public class GuardUser {

    @Id
    @Column(name = "guard_user_id")
    private String id;
    private String name; // 보호자 이름
    private String email;
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_user_id")
    private WardUser wardUser; // 보호 대상 (피보호자), 보호자는 여러 명이 하나의 피보호자에게 공유 받음
    
    @Enumerated(EnumType.STRING)
    private UserType userType; // 사용자 유형 (보호자)

    public GuardUser() {
    }

    public GuardUser(String id, String name, String email, String password, UserType userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}
