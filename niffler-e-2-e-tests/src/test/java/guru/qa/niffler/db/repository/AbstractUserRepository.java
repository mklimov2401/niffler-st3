package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserDataEntity;

import java.util.List;

public abstract class AbstractUserRepository implements UserRepository {
    private final AuthUserDAO authUserDAO;
    private final UserDataUserDAO udUserDAO;

    protected AbstractUserRepository(AuthUserDAO authUserDAO, UserDataUserDAO udUserDAO) {
        this.authUserDAO = authUserDAO;
        this.udUserDAO = udUserDAO;
    }

    @Override
    public void createUserForTest(AuthUserEntity user) {
        authUserDAO.createUser(user);
        udUserDAO.createUserInUserData(fromAuthUser(user));
    }

    @Override
    public void removeAfterTest(AuthUserEntity user) {
        UserDataEntity userInUd = udUserDAO.getUserData(user.getUsername());
        udUserDAO.deleteUserByUsernameInUserData(userInUd.getUsername());
        authUserDAO.deleteUser(user);
    }

    private UserDataEntity fromAuthUser(AuthUserEntity user) {
        UserDataEntity userdataUser = new UserDataEntity();
        userdataUser.setUsername(user.getUsername());
        userdataUser.setCurrency(CurrencyValues.RUB);
        return userdataUser;
    }

    @Override
    public List<UserDataEntity> getUsersData(AuthUserEntity user) {
        return udUserDAO.getUsersData(user.getUsername());
    }
}
