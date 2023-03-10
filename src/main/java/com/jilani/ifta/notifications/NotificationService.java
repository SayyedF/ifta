package com.jilani.ifta.notifications;

import com.jilani.ifta.fatwa.Fatwa;
import com.jilani.ifta.fatwa.FatwaService;
import com.jilani.ifta.users.RoleRepository;
import com.jilani.ifta.users.User;
import com.jilani.ifta.users.UserRepository;
import com.jilani.ifta.users.UserService;
import com.jilani.ifta.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class NotificationService {

    private SimpMessagingTemplate template;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public NotificationService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void test(String text) {
        ChatMessage message = new ChatMessage();
        message.setFrom("Notify Service");
        message.setRecipient("mufti");
        message.setText("Searching for: "+text);
        message.setTime(StringUtils.getCurrentTimeStamp());
        this.template.convertAndSendToUser("mufti",
                "/topic/messages",message);
    }

    public Long getNotificationCounter() {
        User user = userService.getCurrentUser();
        if(user != null) {
            return notificationRepository.countAllByRecipient(user);
        }
        return 0l;
    }

    public List<Notification> getNotifications() {
        User user = userService.getCurrentUser();
        if(user != null) {
            return notificationRepository.findAllByRecipient(user);
        }
        return null;
    }

    public Long getNotificationCounter(String username) {
        User user = userRepository.getUserByUsername(username);
        if(user != null) {
            return notificationRepository.countAllByRecipient(user);
        }
        return 0l;
    }

    public void publishNotificationCounter(long count, String username) {
        Counter counter = new Counter(count);
        System.out.println(count);
        this.template.convertAndSendToUser(username,
                "/topic/counter",counter);
    }

    public void publishNotificationCounter(String username) {
        long count = getNotificationCounter(username);
        Counter counter = new Counter(count);
        this.template.convertAndSendToUser(username,
                "/topic/counter",counter);
    }

    public void alertQuestionRaised(String text, long fatwaId) {

        for (User mufti: userService.getAllMuftis()) {
            Notification notification = new Notification();
            notification.setHeading("New question asked!");
            notification.setText(text);
            notification.setRecipient(mufti);
            notification.setFatwaId(fatwaId);
            notificationRepository.save(notification);

            publishNotificationCounter(mufti.getUsername());
            publishNotification(notification);
        }
    }

    public void alertAnswerPublished(String text, User asker) {
        Notification notification = new Notification();
        notification.setHeading("Your question is answered.");
        notification.setText(text);
        notification.setRecipient(asker);
        notificationRepository.save(notification);

        publishNotificationCounter(asker.getUsername());
        publishNotification(notification);
    }

    public void testNotify(String heading, String text) {

        User user = userService.getCurrentUser();
        //User mufti = userRepository.getUserByUsername("mufti");
        for (User mufti: userService.getAllMuftis()) {

            Notification notification = new Notification();
            notification.setHeading(heading);
            notification.setText(text);
            notification.setRecipient(mufti);
            notificationRepository.save(notification);

            publishNotificationCounter(mufti.getUsername());
            publishNotification(notification);
        }
    }

    public void publishNotification(Notification notification) {
        this.template.convertAndSendToUser(notification.getRecipient().getUsername(),
                "/topic/notifications", notification);
    }

    public void removeNotification(Notification notification) {
        this.template.convertAndSendToUser(notification.getRecipient().getUsername(),
                "/topic/seen", notification);
        notificationRepository.delete(notification);
        publishNotificationCounter(notification.getRecipient().getUsername());
    }

    public void unPublish(long fatwaId, User recipient) {
        Notification notification = notificationRepository.findByRecipientAndFatwaIdAndSeenFalse(recipient, fatwaId);
        if(notification != null) {
            removeNotification(notification);
        }
    }

    public void unPublish(long fatwaId) {
        List<Notification> notifications = notificationRepository.findAllByFatwaIdAndSeenFalse(fatwaId);
        notifications.forEach( notification ->  {
            removeNotification(notification);
        });
    }
}
