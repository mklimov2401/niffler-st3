package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.NavigationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.User.UserType.*;

@Disabled
public class InvitationReceivedWebTest extends BaseWebTest {

    private LoginPage loginPage = new LoginPage();
    private NavigationPage navigationPage = new NavigationPage();
    private FriendsPage friendsPage = new FriendsPage();
    private AllPeoplePage peoplePage = new AllPeoplePage();

    @BeforeEach
    void doLogin(@User(userType = INVITATION_RECEIVED) UserJson userForTest) {
        loginPage.signIn(userForTest);
    }

    @Test
    void invitationReceivedShouldBeDisplayedInTableFriends() {
        friendsPage = navigationPage
                .goToFriends();
        friendsPage.checkingThatListExist();
        friendsPage.checkingInvitationReceived();
    }

    @Test
    void invitationReceivedShouldBeDisplayedInTableSubmitInvited() {
        peoplePage = navigationPage
                .goToAllPeople();
        peoplePage.checkingDisplayedSubmitInvitation();
    }

    @Test
    void invitationReceivedShouldBeDisplayedInTableSubmitDeclined() {
        peoplePage = navigationPage
                .goToAllPeople();
        peoplePage.checkingDisplayedSubmitDeclined();
    }
}
