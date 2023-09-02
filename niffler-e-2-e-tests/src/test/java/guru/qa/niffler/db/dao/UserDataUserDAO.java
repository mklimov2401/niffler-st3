package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.userdata.UserDataEntity;

import java.util.List;
import java.util.UUID;

public interface UserDataUserDAO {

    int createUserInUserData(UserDataEntity user);
  
    void deleteUserByIdInUserData(UUID userId);
  
    void deleteUserByUsernameInUserData(String username);

    UserDataEntity getUserData(String username);

    List<UserDataEntity> getUsersData(String username);

    void updateUserData(UserDataEntity user);


}
