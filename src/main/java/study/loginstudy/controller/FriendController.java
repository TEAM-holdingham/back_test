package study.loginstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.domain.entity.FriendRequest;
import study.loginstudy.service.FriendService;
import study.loginstudy.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String friendsPage() {
        return "friends";
    }

    @GetMapping("/user/principal")
    @ResponseBody
    public ResponseEntity<String> getPrincipalName(Principal principal) {
        return ResponseEntity.ok(principal.getName());
    }

    @PostMapping("/request")
    @ResponseBody
    public ResponseEntity<?> sendFriendRequest(Principal principal, @RequestParam String receiverNickname) {
        String senderLoginId = principal.getName();
        System.out.println("Send request: Sender loginId: " + senderLoginId + ", Receiver nickname: " + receiverNickname);
        friendService.sendFriendRequest(senderLoginId, receiverNickname);
        return ResponseEntity.ok("Friend request sent to " + receiverNickname);
    }

    @PostMapping("/respond")
    @ResponseBody
    public ResponseEntity<?> respondToFriendRequest(Principal principal, @RequestParam String senderNickname, @RequestParam boolean accepted) {
        String receiverLoginId = principal.getName();
        System.out.println("Respond request: Sender nickname: " + senderNickname + ", Receiver loginId: " + receiverLoginId + ", Accepted: " + accepted);
        friendService.respondToFriendRequest(senderNickname, receiverLoginId, accepted);
        return ResponseEntity.ok("Response recorded");
    }

    @GetMapping("/pending")
    @ResponseBody
    public ResponseEntity<List<FriendRequest>> getPendingRequests(Principal principal) {
        String receiverLoginId = principal.getName();
        List<FriendRequest> requests = friendService.getPendingRequests(receiverLoginId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/friendsList")
    @ResponseBody
    public ResponseEntity<List<FriendRequest>> getFriends(Principal principal) {
        String loginId = principal.getName();
        List<FriendRequest> friends = friendService.getFriends(loginId);
        return ResponseEntity.ok(friends);
    }
}
