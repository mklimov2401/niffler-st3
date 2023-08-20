package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.NavigationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.User.UserType.INVITATION_RECEIVED;

public class InvitationReceivedWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = INVITATION_RECEIVED) UserJson userForTest) {
        new LoginPage().signIn(userForTest);
    }

    @Test
    void invitationReceivedShouldBeDisplayedInTableFriends(@User(userType = INVITATION_RECEIVED) UserJson userForTest) {
        FriendsPage friendsPage = new NavigationPage()
                .currentUserDisplayInReport(userForTest)
                .goToFriends();
        friendsPage.checkingThatListExist();
        friendsPage.checkingInvitationReceived();
    }

    @Test
    void invitationReceivedShouldBeDisplayedInTableSubmitInvited(@User(userType = INVITATION_RECEIVED) UserJson userForTest) {
        AllPeoplePage peoplePage = new NavigationPage()
                .currentUserDisplayInReport(userForTest)
                .goToAllPeople();
        peoplePage.checkingDisplayedSubmitInvitation();
    }

    @Test
    void invitationReceivedShouldBeDisplayedInTableSubmitDeclined(@User(userType = INVITATION_RECEIVED) UserJson userForTest) {
        AllPeoplePage peoplePage = new NavigationPage()
                .currentUserDisplayInReport(userForTest)
                .goToAllPeople();
        peoplePage.checkingDisplayedSubmitDeclined();
    }
}
