package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.NavigationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.User.UserType.WITH_FRIENDS;

public class FriendsWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = WITH_FRIENDS) UserJson userForTest) {
        new LoginPage().signIn(userForTest);
    }


    @Test
    void friendShouldBeDisplayedInTable(@User(userType = WITH_FRIENDS) UserJson userForTest) {
        FriendsPage friendsPage = new NavigationPage()
                .currentUserDisplayInReport(userForTest)
                .goToFriends();
        friendsPage.checkingThatListExist();
        friendsPage.checkingYouFriends();
    }


}
