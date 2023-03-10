package com.jilani.ifta.notifications;

import com.jilani.ifta.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByRecipient(User recipient);

    long countAllByRecipient(User recipient);

    Notification findByRecipientAndFatwaIdAndSeenFalse(User recipient, Long fatwaId);

    List<Notification> findAllByFatwaIdAndSeenFalse(long fatwaId);
}
