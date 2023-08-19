package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.NavigationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.User.UserType.*;

public class InvitationSentWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = INVITATION_SENT) UserJson userForTest) {
        new LoginPage().signIn(userForTest);
    }

    @Test
    void invitationSentShouldBeDisplayedInTable(@User(userType = INVITATION_SENT) UserJson userForTest) {
        AllPeoplePage peoplePage = new NavigationPage()
                .currentUserDisplayInReport(userForTest)
                .goToAllPeople();
        peoplePage.checkingPendingInvitation();
    }
}
