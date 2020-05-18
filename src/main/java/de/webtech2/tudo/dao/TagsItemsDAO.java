package de.webtech2.tudo.dao;

import de.webtech2.tudo.model.Item;
import de.webtech2.tudo.model.Tag;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class TagsItemsDAO{

    @PersistenceContext
    private EntityManager entityManager;

    public static void addRel(Tag t, Item i){
        i.getTags().add(t);
    }

    public static void removeRel(Tag t, Item i){
        i.getTags().remove(t);
    }

    public static void removeAllTags(Item i) {
        i.getTags().clear();
    }

    public static void removeAllItems(Tag t){
        final ItemDAO itemDAO = DAOCreator.create(ItemDAO.class);
        List<Item> items = itemDAO.getAll();
        for(Item i:items){
            i.getTags().remove(t);
        }
    }
}
