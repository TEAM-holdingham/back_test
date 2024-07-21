package study.loginstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;
import study.loginstudy.domain.dto.ChatMessage;
import study.loginstudy.domain.entity.ChatMessageEntity;
import study.loginstudy.repository.ChatMessageRepository;
import study.loginstudy.repository.ChatRoomRepository;
import study.loginstudy.repository.UserRepository;
import study.loginstudy.domain.entity.User;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;  // UserRepository 추가

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    @Transactional
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null in sendMessage");
            return;
        }
        String sender = principal.getName();
        chatMessage.setSender(sender);
        chatMessage.setType("CHAT");

        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setSender(sender);
        chatMessageEntity.setReceiver(chatMessage.getReceiver());
        chatMessageEntity.setContent(chatMessage.getContent());
        chatMessageEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));
        chatMessageEntity.setRoomId(chatMessage.getRoomId());

        try {
            chatMessageRepository.save(chatMessageEntity);
            System.out.println("Message saved to DB: " + chatMessageEntity);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error saving message to DB: " + e.getMessage());
        }

        // Send message to the room
        messagingTemplate.convertAndSend("/topic/messages/" + chatMessage.getRoomId(), chatMessage);

        System.out.println("Sent message: " + chatMessage);
    }

    @MessageMapping("/chat.addUser")
    @Transactional
    public void addUser(@Payload ChatMessage chatMessage, Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null in addUser");
            return;
        }
        String sender = principal.getName();
        chatMessage.setSender(sender);
        chatMessage.setContent(sender + " joined!");
        chatMessage.setType("JOIN");
        chatMessage.setRoomId(chatMessage.getRoomId());

        messagingTemplate.convertAndSend("/topic/messages/" + chatMessage.getRoomId(), chatMessage);
        System.out.println("User added: " + chatMessage);
    }

    @MessageMapping("/chat.createRoom")
    public void createRoom(@Payload ChatMessage chatMessage, Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null in createRoom");
            return;
        }
        String roomId = UUID.randomUUID().toString();
        chatMessage.setRoomId(roomId);

        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/roomId", chatMessage);
        System.out.println("Created room: " + roomId);
    }

    @GetMapping("/chat")
    public String chatPage(@RequestParam String friend, Model model, Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null in chatPage");
            return "redirect:/login";
        }
        model.addAttribute("friend", friend);
        model.addAttribute("username", principal.getName());
        return "Chat";
    }

    @GetMapping("/chat/history")
    @ResponseBody
    public List<ChatMessage> getChatHistory(@RequestParam String friend, @RequestParam String roomId, Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null in getChatHistory");
            return null;
        }
        String username = principal.getName();
        List<ChatMessageEntity> chatMessages = chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
        return chatMessages.stream().map(msg -> new ChatMessage(msg.getSender(), msg.getReceiver(), msg.getContent(), msg.getTimestamp().toString(), msg.getRoomId(), msg.getType())).collect(Collectors.toList());
    }

    @GetMapping("/chat/roomId")
    @ResponseBody
    public Map<String, String> getRoomId(@RequestParam String friend, Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Principal is null in getRoomId");
        }
        String senderLoginId = principal.getName();  // 현재 사용자의 로그인 ID를 가져옴
        String senderNickname = getNicknameByLoginId(senderLoginId);  // 현재 사용자의 닉네임을 가져옴
        String receiverNickname = friend;  // 친구의 닉네임은 직접 제공됨
        String receiverLoginId = getLoginIdByNickname(receiverNickname);  // 친구의 로그인 ID를 가져오는 로직 필요

        // 두 사용자의 로그인 ID와 닉네임을 사전 순으로 정렬하여 일관된 roomID 생성
        String roomId = generateRoomId(senderLoginId, senderNickname, receiverLoginId, receiverNickname);

        Map<String, String> response = new HashMap<>();
        response.put("roomId", roomId);
        return response;
    }

    private String generateRoomId(String id1, String nickname1, String id2, String nickname2) {
        List<String> keys = Arrays.asList(id1 + nickname1, id2 + nickname2);
        Collections.sort(keys);
        return String.join("-", keys);
    }

    private String getNicknameByLoginId(String loginId) {
        // loginId를 기반으로 닉네임을 조회하는 로직
        Optional<User> user = userRepository.findByLoginId(loginId);
        return user.map(User::getNickname).orElseThrow(() -> new RuntimeException("User not found for loginId: " + loginId));
    }

    private String getLoginIdByNickname(String nickname) {
        // nickname을 기반으로 로그인 ID를 조회하는 로직
        Optional<User> user = userRepository.findByNickname(nickname);
        return user.map(User::getLoginId).orElseThrow(() -> new RuntimeException("User not found for nickname: " + nickname));
    }
}
