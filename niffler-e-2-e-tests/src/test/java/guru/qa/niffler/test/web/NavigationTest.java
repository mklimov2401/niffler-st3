package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.jupiter.annotation.GenerateUser;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.NavigationPage;
import org.junit.jupiter.api.Test;


public class NavigationTest extends BaseWebTest {

    private final LoginPage loginPage = new LoginPage();

    private final NavigationPage nav = new NavigationPage();

    @ApiLogin(
            user = @GenerateUser
    )
    @Test
    void goToFriendsPage() {
        loginPage.openMain();
        nav
                .goToFriends()
                .checkingFreindsPage();
    }

    @ApiLogin(
            user = @GenerateUser
    )
    @Test
    void goToAllPeoplePage() {
        loginPage.openMain();
        nav
                .goToAllPeople()
                .checkingAllPeoplePage();
    }

    @ApiLogin(
            user = @GenerateUser
    )
    @Test
    void goToProfilePage() {
        loginPage.openMain();
        nav
                .goToProfile()
                .checkingProfilePage();
    }
    @ApiLogin(
            user = @GenerateUser
    )
    @Test
    void goToMainPage() {
        loginPage.openMain();
        nav.goToProfile()
                .checkingProfilePage();
        nav.goToMain()
                .checkingMainPage();
    }
}
