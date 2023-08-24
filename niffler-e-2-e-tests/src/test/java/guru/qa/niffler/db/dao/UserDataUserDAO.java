package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.UUID;

public interface UserDataUserDAO {

    int createUserInUserData(UserEntity user);
    void deleteUserByIdInUserData(UUID userId);
    void deleteUserByUsernameInUserData(String username);

    UserDataEntity getUserData(String username);

    void updateUserData(UserJson user);


}
