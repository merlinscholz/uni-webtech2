package de.webtech2.tudo.dao;

import org.apache.shiro.authc.AuthenticationException;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import java.util.Set;

public class DAOCreator {
    public static <T extends GenericDAO> T create(Class<T> tClass){
        final BeanManager bm = CDI.current().getBeanManager();
        final Set<Bean<?>> beans = bm.getBeans(tClass);
        if(beans.isEmpty()){throw new AuthenticationException();}
        final Bean<T> bean = (Bean<T>) bm.resolve(beans);
        final CreationalContext<T> cctx = bm.createCreationalContext(bean);
        return tClass.cast(bm.getReference(bean, tClass, cctx));
    }
}
