package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.NavigationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.User.UserType.*;

public class InvitationReceivedWebTest extends BaseWebTest {

    NavigationPage navigationPage = new NavigationPage();
    FriendsPage friendsPage = new FriendsPage();
    AllPeoplePage peoplePage = new AllPeoplePage();

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
