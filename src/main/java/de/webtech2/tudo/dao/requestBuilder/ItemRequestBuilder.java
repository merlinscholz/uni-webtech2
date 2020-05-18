package de.webtech2.tudo.dao.requestBuilder;

import de.webtech2.tudo.model.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemRequestBuilder {

    public static enum SortOrder {
        ASC, DESC
    }

    public static enum SortCriteria {
        ID, TITLE, DESCRIPTION, CREATED
    }

    @PersistenceContext
    EntityManager entityManager;

    private Integer id;
    private String title;
    private String description;
    private boolean done;
    private Date finished;
    private Date createdSince;
    private User user;
    private User owner;
    private User assignee;
    private Tag tag;
    private SortOrder sortOrder;
    private SortCriteria sortCriteria;

    public List<Item> run() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = criteriaBuilder.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        Root<Tag> tagRoot = criteriaQuery.from(Tag.class);
        ArrayList<Predicate> predicates = new ArrayList<Predicate>();

        if(user!=null){
            predicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.<User>get(Item_.owner), user),
                            criteriaBuilder.equal(root.<User>get(Item_.assignee), user)));
        }

        if(id!=null){
            predicates.add(
                    criteriaBuilder.equal(root.<Integer>get(Item_.id), id));
        }

        if(title!=null&&!title.isEmpty()){
            predicates.add(
                    criteriaBuilder.like(
                            root.<String>get(Item_.title),
                            "%"+title+"%"));
        }

        if(createdSince!=null){
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                            root.<Date>get(Item_.created),
                            createdSince));
        }

        if(owner!=null){
            predicates.add(
                    criteriaBuilder.equal(
                            root.<User>get(Item_.owner), owner));
        }

        if(assignee!=null){
            predicates.add(
                    criteriaBuilder.equal(
                            root.<User>get(Item_.assignee), assignee));
        }

        if(tag!=null){
            SetJoin<Item, Tag> tags = root.join(Item_.tags);
            predicates.add(
                    tags.get(Tag_.id).in(tag.getId()));
        }

        if(sortCriteria!=null){
            if(sortCriteria==SortCriteria.ID){
                if(sortOrder==ItemRequestBuilder.SortOrder.DESC){
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Item_.id)));
                }else{
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Item_.id)));
                }
            }else if(sortCriteria==SortCriteria.TITLE){
                if(sortOrder==ItemRequestBuilder.SortOrder.DESC){
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Item_.title)));
                }else{
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Item_.title)));
                }
            }else if(sortCriteria==SortCriteria.DESCRIPTION){
                if(sortOrder==ItemRequestBuilder.SortOrder.DESC){
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Item_.description)));
                }else{
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Item_.description)));
                }
            }else if(sortCriteria==SortCriteria.CREATED){
                if(sortOrder==ItemRequestBuilder.SortOrder.DESC){
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Item_.created)));
                }else{
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Item_.created)));
                }
            }
        }

        Predicate[] predicateArray = new Predicate[predicates.size()];
        predicateArray = predicates.toArray(predicateArray);
        criteriaQuery.where(predicateArray);
        criteriaQuery.distinct(true);
        CriteriaQuery<Item> select = criteriaQuery.select(root);
        return entityManager.createQuery(select).getResultList();
    }


    public ItemRequestBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public ItemRequestBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public ItemRequestBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ItemRequestBuilder setDone(boolean done) {
        this.done = done;
        return this;
    }

    public ItemRequestBuilder setFinished(Date finished) {
        this.finished = finished;
        return this;
    }

    public ItemRequestBuilder setCreatedSince(Date createdSince) {
        this.createdSince = createdSince;
        return this;
    }

    public ItemRequestBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public ItemRequestBuilder setOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public ItemRequestBuilder setAssignee(User assignee) {
        this.assignee = assignee;
        return this;
    }

    public ItemRequestBuilder setTag(Tag tag) {
        this.tag = tag;
        return this;
    }

    public ItemRequestBuilder setSortOrder(ItemRequestBuilder.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public ItemRequestBuilder setSortCriteria(ItemRequestBuilder.SortCriteria sortCriteria) {
        this.sortCriteria = sortCriteria;
        return this;
    }
}
