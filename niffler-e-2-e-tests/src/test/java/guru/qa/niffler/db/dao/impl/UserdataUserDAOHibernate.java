package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.userdata.UserDataEntity;

import java.util.List;
import java.util.UUID;

public class UserdataUserDAOHibernate extends JpaService implements UserDataUserDAO {

    public UserdataUserDAOHibernate() {
        super(EntityManagerFactoryProvider.INSTANCE.getDataSource(ServiceDB.USERDATA).createEntityManager());
    }


    @Override
    public int createUserInUserData(UserDataEntity user) {
        persist(user);
        return 0;
    }

    @Override
    public void deleteUserByIdInUserData(UUID userId) {
        UserDataEntity user = em.createQuery("select u from UserDataEntity u where u.id=:userId",
                        UserDataEntity.class)
                .setParameter("userId", userId)
                .getSingleResult();
        remove(user);
    }

    @Override
    public void deleteUserByUsernameInUserData(String username) {
        remove(getUserData(username));
    }

    @Override
    public UserDataEntity getUserData(String username) {
        return em.createQuery("select u from UserDataEntity u where u.username=:username",
                        UserDataEntity.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    @Override
    public List<UserDataEntity> getUsersData(String username) {
        return em.createQuery("select u from UserDataEntity u where u.username=:username",
                        UserDataEntity.class)
                .setParameter("username", username)
                .getResultList();
    }

    @Override
    public void updateUserData(UserDataEntity user) {
        merge(user);
    }
}
