package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.AuthorityEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthUserDAOHibernate extends JpaService implements AuthUserDAO {

    public AuthUserDAOHibernate() {
        super(EntityManagerFactoryProvider.INSTANCE.getDataSource(ServiceDB.AUTH).createEntityManager());
    }

    @Override
    public int createUser(AuthUserEntity user) {
        user.setPassword(pe.encode(user.getPassword()));
        persist(user);
        return 1;
    }

    @Override
    public AuthUserEntity updateUser(AuthUserEntity user) {
        AuthUserEntity userByName = getUserByName(user.getUsername());
        return merge(userByName);
    }

    @Override
    public void deleteUser(AuthUserEntity user) {
        AuthUserEntity userByName = getUserByName(user.getUsername());
        remove(userByName);
    }

    @Override
    public AuthUserEntity getUserById(UUID userId) {
        return em.createQuery("select u from AuthUserEntity u where u.id=:userId",
                        AuthUserEntity.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Override
    public AuthUserEntity getUserByName(String name) {
        return em.createQuery("select u from AuthUserEntity u where u.username=:name",
                        AuthUserEntity.class)
                .setParameter("name", name)
                .getSingleResult();
    }
}
