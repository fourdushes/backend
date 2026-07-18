package tohear.hearo.medicaltreatment.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import tohear.hearo.medicaltreatment.chat.domain.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @EntityGraph(attributePaths = "record")
    List<ChatMessage> findAllByChatRoomIdOrderByCreatedAtAscIdAsc(Long chatRoomId);

    Optional<ChatMessage> findFirstByChatRoomIdOrderByCreatedAtDescIdDesc(Long chatRoomId);
}
