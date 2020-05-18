package de.webtech2.tudo.dao;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

public abstract class GenericDAO<T> {
    @PersistenceContext
    private EntityManager entityManager;

    GenericDAO(){}

    public List getAll(){
        return entityManager.createQuery("SELECT i FROM "+getObjectClassName()+" i")
                .getResultList();
    }

    public <E> void add(E o) {
        entityManager.persist(o);
    }

    public <E> void update(E o) {
        entityManager.merge(o);
    }

    public <E> void delete(E o){
        entityManager.remove(o);
    }

    public boolean exists(int id){
        List<Object> results = entityManager.createQuery("SELECT o FROM "+getObjectClassName()+" o WHERE o.id = :insId")
                .setParameter("insId", id)
                .getResultList();

        if(results.isEmpty()) {
            return false;
        }else if(results.size()==1){
            return true;
        }
        throw new NonUniqueResultException();
    }

    public void deleteAll(){
        entityManager.createQuery("DELETE FROM "+getObjectClassName()).executeUpdate();
    }

    protected abstract String getObjectClassName();
}
