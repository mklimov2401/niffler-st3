package guru.qa.niffler.test;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;


public class NegativeLoginTest extends BaseWebTest {

    private LoginPage loginPage = new LoginPage();

    @Test
    void negativeLogin() {
        loginPage.signIn("NotFoundUser", "12345");
        loginPage.checkLoginError();
    }
}
