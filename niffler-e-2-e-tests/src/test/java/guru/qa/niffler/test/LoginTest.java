package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.annotation.Dao;
import guru.qa.niffler.jupiter.extension.DaoExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ExtendWith(DaoExtension.class)
public class LoginTest extends BaseWebTest {

    @Dao
    private AuthUserDAO authUserDAO;
    @Dao
    private UserDataUserDAO userDataUserDAO;
    private UserEntity user;

    private LoginPage loginPage = new LoginPage();

    @BeforeEach
    void createUser() {
        user = new UserEntity();
        user.setUsername("valentin_5");
        user.setPassword("12345");
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setAuthorities(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    return ae;
                }).toList());
        authUserDAO.createUser(user);
        userDataUserDAO.createUserInUserData(user);
    }

    @AfterEach
    void deleteUser() {
        userDataUserDAO.deleteUserByIdInUserData(user.getId());
        authUserDAO.deleteUserById(user.getId());

    }

    @Test
    void mainPageShouldBeVisibleAfterLogin() {
        loginPage.signIn(user.getUsername(), user.getPassword());
    }
}
