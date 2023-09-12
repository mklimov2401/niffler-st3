package guru.qa.niffler.test;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.jupiter.annotation.RandomUser;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;


public class RegisterTest extends BaseWebTest {

    private LoginPage loginPage = new LoginPage();

    @RandomUser
    @Test
    void register(AuthUserEntity user) {
        loginPage
                .toRegister()
                .enterUsername(user.getUsername())
                .enterPassword(user.getPassword())
                .enterPasswordSubmit(user.getPassword())
                .signUp()
                .checkRegistered();
    }
}
