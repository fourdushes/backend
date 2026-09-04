package tohear.hearo.user.auth.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.Getter;
import tohear.hearo.user.ward.WardUser;

@Entity
@Getter
public class WardUserDisease {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ward_user_id")
    private WardUser wardUser; // 피보호자 아이디

    private int coldCount; // 감기 횟수
    private int coldCount2; // 감기 횟수
    private int coldCount3; // 감기 횟수
    private int coldCount4; // 감기 횟수
    private int coldCount5; // 감기 횟수
    private int coldCount6; // 감기 횟수
    private int coldCount7; // 감기 횟수
    private int coldCount8; // 감기 횟수
    private int coldCount9; // 감기 횟수
    private int coldCount10; // 감기 횟수

    public WardUserDisease() {
    }

    public WardUserDisease(WardUser wardUser) {
        this.wardUser = wardUser;
        this.coldCount = 0;
        this.coldCount2 = 0;
        this.coldCount3 = 0;
        this.coldCount4 = 0;
        this.coldCount5 = 0;
        this.coldCount6 = 0;
        this.coldCount7 = 0;
        this.coldCount8 = 0;
        this.coldCount9 = 0;
        this.coldCount10 = 0;
    }
}
