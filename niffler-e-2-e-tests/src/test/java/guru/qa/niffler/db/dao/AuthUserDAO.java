package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public interface AuthUserDAO {

    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    UUID createUser(UserEntity user);
  
    void deleteUserById(UUID userId);

    void updateUser(UserEntity user);

    UserEntity getUserById(UUID userId);

    UserEntity getUserByName(String username);
}
