package tohear.hearo.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class User {

    @Id
    @Column(name = "user_id")
    private String id;
    private String name; // 환자 이름
    private String email;
    private String password;
    private UserType userType; // 사용자 유형 (피보호자)

    public User() {
    }

    public User(String id, String name, String email, String password, UserType userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

}
