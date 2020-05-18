package de.webtech2.tudo.dao;

import de.webtech2.tudo.model.Item;
import de.webtech2.tudo.model.Tag;
import de.webtech2.tudo.model.User;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.List;

@Transactional
public class UserDAO extends GenericDAO {

    @PersistenceContext
    private EntityManager entityManager;
    ItemDAO itemDAO = DAOCreator.create(ItemDAO.class);

    public UserDAO(){}

    private final PasswordService passwordService = new DefaultPasswordService();

    @Override
    protected String getObjectClassName() {
        return "User";
    }

    public User getByUsername(String name) throws NoResultException{
        List<User> results = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :insUsername")
                .setParameter("insUsername", name)
                .getResultList();

        if(results.isEmpty()) {
            throw new NoResultException();
        }else if(results.size()==1){
            return results.get(0);
        }
        throw new NonUniqueResultException();
    }

    public boolean existsByUsername(String username){
        try{
            if(!username.equals("admin")) {
                getByUsername(username);
            }
        }catch(NoResultException nre){
            return false;
        }
        return true;
    }

    public User createUser(String username, String email, String hashedPassword){
        if(!existsByUsername(username)){
            String shiroPasswordHash = passwordService.encryptPassword(hashedPassword);
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(shiroPasswordHash);
            this.add(user);

            TagDAO tagDAO = DAOCreator.create(TagDAO.class);

            Tag home = new Tag();
            home.setName("Home");
            home.setColor("333333");
            home.setOwner(user);
            tagDAO.add(home);

            Tag work = new Tag();
            work.setName("Work");
            work.setColor("333333");
            work.setOwner(user);
            tagDAO.add(work);

            Tag hobby = new Tag();
            hobby.setName("Hobby");
            hobby.setColor("333333");
            hobby.setOwner(user);
            tagDAO.add(hobby);

            return user;
        }
        return null;
    }

    public void deleteUser(User user) {
        List<Item> userItems = itemDAO.getByUser(user);
        for(Iterator<Item> iter = userItems.iterator();iter.hasNext();){
            Item el = iter.next();
            itemDAO.delete(el);
            iter.remove();
        }
        delete(user);
    }

    @Override
    public void update(Object o){
        User user = (User)o;
        switch (user.getPassword().length()) {
            case 64:
                String shiroPasswordHash = passwordService.encryptPassword(user.getPassword());
                user.setPassword(shiroPasswordHash);
                entityManager.merge(o);
                break;
            case 92:
                entityManager.merge(o);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
}
