package com.jilani.ifta.controllers;

import com.jilani.ifta.notifications.ChatMessage;
import com.jilani.ifta.notifications.Notification;
import com.jilani.ifta.notifications.NotificationRepository;
import com.jilani.ifta.notifications.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.websocket.server.PathParam;

@Controller
public class NotificationController {

    private SimpMessagingTemplate template;

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    public NotificationController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @GetMapping("/stomp-broadcast")
    public String getWebSocketBroadcast() {
        return "stomp-broadcast";
    }

    @GetMapping("/remove")
    public ModelAndView removeNotification(@RequestParam Long id) {
        notificationService.removeNotification(notificationRepository.getById(id));
        return new ModelAndView("redirect:/");
    }

    @MessageMapping("/broadcast")
    @SendTo("/topic/messages")
    public ChatMessage send(ChatMessage chatMessage) throws Exception {
        return new ChatMessage(chatMessage.getFrom(), chatMessage.getText(), "ALL");
    }
}
