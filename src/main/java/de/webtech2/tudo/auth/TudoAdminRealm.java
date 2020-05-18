package de.webtech2.tudo.auth;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.HashSet;
import java.util.Set;


public class TudoAdminRealm extends AuthorizingRealm implements Realm {

    final static String REALM = "TUDOADMIN";


    @Override
    public AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;

        String username = upToken.getUsername();
        if(username.equals("admin")){   // todo move to external file
            return new SimpleAuthenticationInfo(username, "8C6976E5B5410415BDE908BD4DEE15DFB167A9C873FC4BB8A81F6F2AB448A918".toLowerCase().toCharArray(), getName());
        }
        throw new UnknownAccountException("Not an admin account!");
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String)principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        if(username.equals("admin")){
            Set<String> permissions = new HashSet<>();
            permissions.add("*");
            authorizationInfo.setStringPermissions(permissions);

            Set<String> roles = new HashSet<>();
            roles.add("admin");
            authorizationInfo.setRoles(roles);
        }

        return authorizationInfo;
    }

}
