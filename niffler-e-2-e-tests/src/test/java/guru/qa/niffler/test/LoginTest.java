package guru.qa.niffler.test;

import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;


public class LoginTest extends BaseWebTest {

    private LoginPage loginPage = new LoginPage();

    @DBUser
    @Test
    void mainPageShouldBeVisibleAfterLogin(UserEntity user) {
        loginPage.signIn(user.getUsername(), user.getPassword());
    }
}
