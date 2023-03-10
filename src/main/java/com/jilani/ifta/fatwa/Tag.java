package com.jilani.ifta.fatwa;

import javax.persistence.*;
import java.util.List;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String tagName;
    private Long count;

    public Tag() {
    }

    public Tag(String tagName, Long count) {
        this.tagName = tagName;
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<Fatwa> getFatwaList() {
        return fatwaList;
    }

    public void setFatwaList(List<Fatwa> fatwaList) {
        this.fatwaList = fatwaList;
    }

    @ManyToMany
    @JoinTable(name = "fatwa_tags",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "fatwa_id"))
    private List<Fatwa> fatwaList;

/*
    @ManyToOne
    private String parentTag;

    @OneToMany
    private List<Tag> similarTags;
*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public long incrementCounterBy(int numberOfFatawa) {
        this.count += numberOfFatawa;
        return count;
    }
}
