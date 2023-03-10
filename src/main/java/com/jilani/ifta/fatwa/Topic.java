package com.jilani.ifta.fatwa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private Date date;
    private long counter = 0l;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "topic")
    private List<Fatwa> fatwaList = new ArrayList<>();

    public Topic() {}

    public Topic(String name) {
        this.name = name;
    }

    public List<Fatwa> getFatwaList() {
        return fatwaList;
    }

    public void setFatwaList(List<Fatwa> fatwaList) {
        this.fatwaList = fatwaList;
    }

    public Category getCategory() {
        return category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void addFatwa(Fatwa fatwa, CategoryRepository categoryRepository) {
        this.fatwaList.add(fatwa);
        this.counter++;
        Category category = this.category;

    }

    public long incrementCounterBy(int numberOfNewFatwa) {
        this.counter+= numberOfNewFatwa;
        return counter;
    }

    public long decrementCounterBy(int numberOfNewFatwa) {
        this.counter-= numberOfNewFatwa;
        return counter;
    }
}
