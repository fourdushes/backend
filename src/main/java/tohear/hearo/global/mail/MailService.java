package tohear.hearo.global.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // 랜덤으로 숫자 생성
    private int createNumber() {
        return ThreadLocalRandom.current().nextInt(100000, 1000000);
    }

    public MimeMessage createMail(String mail, int number) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");

            String body = "<h3>" + "요청하신 인증 번호입니다." + "</h3>"
                        + "<h1>" + number + "</h1>"
                        + "<h3>" + "원래 창으로 돌아가 인증번호를 입력해주세요.(3분 이내)" + "</h3>";

            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("메일 생성에 실패했습니다.", e);
        }

        return message;
    }

    public void sendMail(String mail) {

        int checkNumber = createNumber();
        MimeMessage message = createMail(mail, checkNumber);
        javaMailSender.send(message);

        // Redis에 (Key: 이메일, Value: 인증번호) 저장 및 3분 유효시간(TTL) 설정
        redisTemplate.opsForValue().set(
                "mail:" + mail, 
                String.valueOf(checkNumber), 
                Duration.ofMinutes(3)
        );
    }

    public boolean checkCode(String mail, String checkNumber) {
        String savedCode = redisTemplate.opsForValue().get("mail:" + mail);
        
        // Redis에 저장된 번호가 없거나(만료됨), 입력한 번호와 다르면 false 리턴
        boolean isCorrect = savedCode != null && savedCode.equals(checkNumber);

        if (isCorrect) {
            redisTemplate.delete("mail:" + mail); // 인증 성공 시 Redis에서 데이터 삭제
        }

        return isCorrect;
    }
}
