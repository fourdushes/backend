package tohear.hearo.texttalk.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tohear.hearo.texttalk.domain.TextTalkResponse;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;
import tohear.hearo.user.ward.WardUser;
import tohear.hearo.user.ward.WardUserRepository;

@Service
@RequiredArgsConstructor
public class TextTalkService {

    private final WardUserRepository wardUserRepository;

    public TextTalkResponse showTextTalk(MedicalUserPrincipal principal) {

        if (principal.getUserType() != UserType.WARD) {
            throw new IllegalArgumentException("피보호자에게만 필담 화면이 제공됩니다.");
        }

        WardUser findWardUser = wardUserRepository.findById(principal.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("피보호자 정보를 찾을 수 없습니다."));

        return new TextTalkResponse(findWardUser.getId(), findWardUser.getName(), findWardUser.getBirthDate().toString());
    }

}
