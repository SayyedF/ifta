package com.jilani.ifta.users;

import com.jilani.ifta.fatwa.Fatwa;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private boolean enabled;

    //fullName, email, address, city, state, country, zipcode
    private String fullName;
    private String email;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipcode;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "asker")
    private List<Fatwa> questions = new ArrayList<>();

    private Long questionCount;

    @OneToMany
    @JoinTable(name = "fatwa_writers",
            joinColumns = @JoinColumn(name = "fatwa_id"),
            inverseJoinColumns = @JoinColumn(name = "answered_by_user_id"))
    private List<Fatwa> answers = new ArrayList<>();

    private Long answerCount;

    @OneToMany(mappedBy = "mufti")
    public List<Fatwa> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Fatwa> fatwaList) {
        this.answers = fatwaList;
    }

    public Long getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Long answerCount) {
        this.answerCount = answerCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public List<Fatwa> getQuestions() {
        return questions;
    }

    public Long getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Long questionCount) {
        this.questionCount = questionCount;
    }

    public void setQuestions(List<Fatwa> questions) {
        this.questions = questions;
    }

    public void addAnswer(Fatwa answer) {
        this.answers.add(answer);
        this.answerCount++;
    }

    public void addQuestion(Fatwa question) {
        this.questions.add(question);
        this.questionCount++;

    }
}
