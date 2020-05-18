package de.webtech2.tudo.dao;

import de.webtech2.tudo.model.Tag;
import de.webtech2.tudo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
public class TagDAO extends GenericDAO {

    @PersistenceContext
    private EntityManager entityManager;
    ItemDAO itemDAO = DAOCreator.create(ItemDAO.class);


    public TagDAO(){}

    @Override
    protected String getObjectClassName() {
        return "Tag";
    }

    public Set<Tag> getByUser(User user) {
        return new HashSet(entityManager.createQuery("SELECT t FROM "+getObjectClassName()+" t WHERE t.owner = :insOwner")
                .setParameter("insOwner", user)
                .getResultList());
    }

    public Tag getById(int id) throws NoResultException{   // todo generalize
        List<Tag> results = entityManager.createQuery("SELECT t FROM Tag t WHERE t.id = :insId")
                .setParameter("insId", id)
                .getResultList();

        if(results.isEmpty()) {
            throw new NoResultException();
        }else if(results.size()==1){
            return results.get(0);
        }
        throw new NonUniqueResultException();
    }

    public void delete(Tag t){
        TagsItemsDAO.removeAllItems(t);
        entityManager.remove(t);
    }
}
