package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.NavigationPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;


public class EnterProfileTest extends BaseWebTest {

    private LoginPage login = new LoginPage();
    private NavigationPage nav = new NavigationPage();

    private ProfilePage profile = new ProfilePage();

    @DBUser
    @Test
    void enterProfile(AuthUserEntity user) {
        String name = "Dima";
        String surname = "Ivanov";
        login.signIn(user.getUsername(), user.getPassword());
        nav.goToProfile();
        profile.enterName(name);
        profile.enterSurname(surname);
        profile.submit();
        Selenide.refresh();
        profile.checkEnterProfile(name, surname);
    }
}
