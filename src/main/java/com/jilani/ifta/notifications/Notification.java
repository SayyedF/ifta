package com.jilani.ifta.notifications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jilani.ifta.users.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@JsonIgnoreProperties({"recipient", "hibernateLazyInitializer"})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String heading, text, url, type;

    private String sender;

    private Long fatwaId;

    @ManyToOne(fetch = FetchType.EAGER)
    private User recipient;

    private boolean seen = false;
    private boolean handled = false;

    public Long getFatwaId() {
        return fatwaId;
    }

    public void setFatwaId(Long fatwaId) {
        this.fatwaId = fatwaId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String from) {
        this.sender = from;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
