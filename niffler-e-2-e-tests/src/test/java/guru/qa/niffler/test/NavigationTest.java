package guru.qa.niffler.test;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.NavigationPage;
import org.junit.jupiter.api.Test;


public class NavigationTest extends BaseWebTest {

    private LoginPage loginPage = new LoginPage();

    private NavigationPage nav = new NavigationPage();

    @DBUser
    @Test
    void goToFriendsPage(AuthUserEntity user) {
        loginPage.signIn(user.getUsername(), user.getPassword());
        nav
                .goToFriends()
                .checkingFreindsPage();
    }

    @DBUser
    @Test
    void goToAllPeoplePage(AuthUserEntity user) {
        loginPage.signIn(user.getUsername(), user.getPassword());
        nav
                .goToAllPeople()
                .checkingAllPeoplePage();
    }

    @DBUser
    @Test
    void goToProfilePage(AuthUserEntity user) {
        loginPage.signIn(user.getUsername(), user.getPassword());
        nav
                .goToProfile()
                .checkingProfilePage();
    }
    @DBUser
    @Test
    void goToMainPage(AuthUserEntity user) {
        loginPage.signIn(user.getUsername(), user.getPassword());
        nav.goToProfile()
                .checkingProfilePage();
        nav.goToMain()
                .checkingMainPage();
    }
}
