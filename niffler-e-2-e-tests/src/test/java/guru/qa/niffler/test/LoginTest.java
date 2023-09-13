package guru.qa.niffler.test;

import guru.qa.niffler.db.logging.AllureSqlLogger;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;


public class LoginTest extends BaseWebTest {

    private LoginPage loginPage = new LoginPage();

    @DBUser
    @Test
    void mainPageShouldBeVisibleAfterLogin(AuthUserEntity user) {
        loginPage.signIn(user.getUsername(), user.getPassword());
        new AllureSqlLogger().testLogSql("MEGA TEST");
    }
}
