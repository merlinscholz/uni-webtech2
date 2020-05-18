package de.webtech2.tudo.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    // Salt is also stored in here
    @Column(nullable = false)
    private String password;

    @Column
    private String email;

    @Column
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "owner", orphanRemoval = true)
    private List<Item> items;

    @Column
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "assignee")
    private List<Item> assigned;

    @Column
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "owner", orphanRemoval = true)
    private List<Tag> tags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User redact(){
        User newUser = new User();
        newUser.setEmail(this.getEmail());
        newUser.setUsername(this.getUsername());
        return newUser;
    }

}
