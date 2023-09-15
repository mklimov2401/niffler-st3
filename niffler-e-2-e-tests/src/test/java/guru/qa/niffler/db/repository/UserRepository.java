package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserDataEntity;

import java.util.List;

public interface UserRepository {

    void createUserForTest(AuthUserEntity user);

    void removeAfterTest(AuthUserEntity user);

    List<UserDataEntity> getUsersData(AuthUserEntity user);
}
