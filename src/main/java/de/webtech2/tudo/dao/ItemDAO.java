package de.webtech2.tudo.dao;

import de.webtech2.tudo.dao.requestBuilder.ItemRequestBuilder;
import de.webtech2.tudo.model.Item;
import de.webtech2.tudo.model.User;
import org.apache.shiro.authc.AuthenticationException;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Transactional
public class ItemDAO extends GenericDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public ItemDAO(){}

    @Override
    protected String getObjectClassName() {
        return "Item";
    }

    public List<Item> getByUser(User user){
        ItemRequestBuilder itemRequestBuilder = this.getRequestBuilder();
        itemRequestBuilder.setUser(user);
        return itemRequestBuilder.run();
    }

    public Item getById(int id) throws NoResultException{
        ItemRequestBuilder itemRequestBuilder = this.getRequestBuilder();
        itemRequestBuilder.setId(id);
        List<Item> results = itemRequestBuilder.run();

        if(results.isEmpty()) {
            throw new NoResultException();
        }else if(results.size()==1){
            return results.get(0);
        }
        throw new NonUniqueResultException();
    }

    public void delete(Item i){
        TagsItemsDAO.removeAllTags(i);
        entityManager.remove(i);
    }

    public static ItemRequestBuilder getRequestBuilder(){
        final BeanManager bm = CDI.current().getBeanManager();
        final Set<Bean<?>> beans = bm.getBeans(ItemRequestBuilder.class);
        if(beans.isEmpty()){throw new AuthenticationException();}
        final Bean<ItemRequestBuilder> bean = (Bean<ItemRequestBuilder>) bm.resolve(beans);
        final CreationalContext<ItemRequestBuilder> cctx = bm.createCreationalContext(bean);
        return (ItemRequestBuilder)(bm.getReference(bean, ItemRequestBuilder.class, cctx));
    }
}
