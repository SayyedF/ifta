package com.jilani.ifta.fatwa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private Date date;
    private long counter = 0l;

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<Topic> topics = new ArrayList<>();

    public Category() {
    }

    public Category(String name) {
        this.name = name;
        this.date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void addTopic(Topic topic){
        this.topics.add(topic);
    }

    public long incrementCounterBy(int numberOfNewFatwas) {
        this.counter+= numberOfNewFatwas;
        return this.counter;
    }

    public long decrementCounterBy(int numberOfNewFatwa) {
        this.counter-= numberOfNewFatwa;
        return counter;
    }
}
