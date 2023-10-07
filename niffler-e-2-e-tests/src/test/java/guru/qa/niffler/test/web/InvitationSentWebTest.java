package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.NavigationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.User.UserType.*;

@Disabled
public class InvitationSentWebTest extends BaseWebTest {
    private LoginPage loginPage = new LoginPage();
    private NavigationPage navigationPage = new NavigationPage();
    private AllPeoplePage peoplePage = new AllPeoplePage();

    @BeforeEach
    void doLogin(@User(userType = INVITATION_SENT) UserJson userForTest) {
        loginPage.signIn(userForTest);
    }

    @Test
    void invitationSentShouldBeDisplayedInTable() {
        peoplePage = navigationPage
                .goToAllPeople();
        peoplePage.checkingPendingInvitation();
    }
}
