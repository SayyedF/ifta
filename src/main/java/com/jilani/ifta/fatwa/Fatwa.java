package com.jilani.ifta.fatwa;

import com.jilani.ifta.users.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Fatwa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    @Lob
    @Column
    private String question;

    @Lob
    @Column
    private String answer;
    private String fatwaId;
    private boolean isAnswered;
    private boolean isApproved;
    private Date askedOn;
    private Date answeredOn;
    private Long viewCount;

    @ManyToOne
    @JoinColumn(name = "asked_by_user_id")
    private User asker;

    @ManyToOne
    @JoinColumn(name = "approved_by_user_id")
    private User approvedBy;

    @ManyToOne
    @JoinTable(name = "fatwa_writers", joinColumns = @JoinColumn(name = "answered_by_user_id"), // bug: it should be
                                                                                                // vice versa
            inverseJoinColumns = @JoinColumn(name = "fatwa_id"))
    private User mufti;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "fatwa_tags", joinColumns = @JoinColumn(name = "fatwa_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getFatwaId() {
        return fatwaId;
    }

    public void setFatwaId(String fatwaId) {
        this.fatwaId = fatwaId;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public Date getAskedOn() {
        return askedOn;
    }

    public void setAskedOn(Date askedOn) {
        this.askedOn = askedOn;
    }

    public Date getAnsweredOn() {
        return answeredOn;
    }

    public void setAnsweredOn(Date answeredOn) {
        this.answeredOn = answeredOn;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public User getAsker() {
        return asker;
    }

    public void setAsker(User asker) {
        this.asker = asker;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public User getMufti() {
        return mufti;
    }

    public void setMufti(User mufti) {
        this.mufti = mufti;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void incrementViewsBy(int count) {
        this.viewCount += count;
    }
}
