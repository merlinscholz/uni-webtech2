package de.webtech2.tudo.auth;

import de.webtech2.tudo.dao.DAOCreator;
import de.webtech2.tudo.dao.ItemDAO;
import de.webtech2.tudo.dao.UserDAO;
import de.webtech2.tudo.dao.requestBuilder.ItemRequestBuilder;
import de.webtech2.tudo.model.Item;
import de.webtech2.tudo.model.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TudoRealm extends AuthorizingRealm implements Realm {

    final static String REALM = "TUDO";
    private final UserDAO userDAO = DAOCreator.create(UserDAO.class);



    @Override
    public AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;

        String username = upToken.getUsername();
        if(username==null||username.isEmpty()){
            throw new UnknownAccountException("Username can not be empty or null");
        }

        User user;

        try {
            user = userDAO.getByUsername(username);
        }catch (NoResultException | NonUniqueResultException nre){
            throw new UnknownAccountException("No account found for user "+username);
        }

        String password = user.getPassword();

        if(password==null||password.isEmpty()){
            throw new UnknownAccountException("No account found for user "+username);
        }

        return new SimpleAuthenticationInfo(username, password.toCharArray(), getName());
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String)principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> permissions = new HashSet<>();
        permissions.add("item:*:"+username);
        permissions.add("tag:*:"+username);
        ItemDAO itemDAO = DAOCreator.create(ItemDAO.class);
        UserDAO userDAO = DAOCreator.create(UserDAO.class);
        ItemRequestBuilder itemRequestBuilder = ItemDAO.getRequestBuilder();
        itemRequestBuilder.setAssignee(userDAO.getByUsername(username));
        List<Item> assigned = itemRequestBuilder.run();
        for(Item item:assigned){
            permissions.add("item:*:"+item.getOwner().getUsername()+":"+item.getId());
        }
        permissions.add("user:*:"+username);
        authorizationInfo.addStringPermissions(permissions);


        return authorizationInfo;
    }

}
