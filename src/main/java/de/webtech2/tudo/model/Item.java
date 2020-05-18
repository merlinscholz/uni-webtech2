package de.webtech2.tudo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean done;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date finished;

    @Column( nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column
    @ManyToMany(fetch = FetchType.EAGER,
        cascade = {CascadeType.MERGE})
    @JoinTable(name="tags_items",
        joinColumns = {@JoinColumn(name="item_id")},
        inverseJoinColumns = {@JoinColumn(name="tag_id")})
    @JsonIgnoreProperties("items")
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"id", "password"})
    private User owner;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"id", "password"})
    private User assignee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User asignee) {
        this.assignee = asignee;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Item(){

    }

    public static int compareTo(Item item, Item item1) {
        return item.getCreated().compareTo(item1.getCreated());
    }

    public static int compareToTitle(Item item, Item item1){
        return item.getTitle().compareTo(item1.getTitle());
    }
}
