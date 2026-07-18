package tohear.hearo.medicaltreatment.chat.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import tohear.hearo.medicaltreatment.record.domain.Record;

@Entity
@Getter
@Table(indexes = @Index(name = "idx_chat_message_room_created", columnList = "chat_room_id,created_at,chat_message_id"))
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageSenderType senderType;

    @Column(nullable = false)
    private String senderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatMessageType messageType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private Record record;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ChatMessage() {
    }

    public ChatMessage(ChatRoom chatRoom, MessageSenderType senderType, String senderId,
                       ChatMessageType messageType, String content, Record record) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("채팅 메시지는 비어 있을 수 없습니다.");
        }
        this.chatRoom = chatRoom;
        this.senderType = senderType;
        this.senderId = senderId;
        this.messageType = messageType;
        this.content = content.trim();
        this.record = record;
        this.createdAt = LocalDateTime.now();
    }
}
