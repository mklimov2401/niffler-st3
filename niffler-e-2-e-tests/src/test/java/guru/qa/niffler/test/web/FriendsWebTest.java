package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.NavigationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.jupiter.annotation.GeneratedUser.Selector.NESTED;
import static guru.qa.niffler.jupiter.annotation.GeneratedUser.Selector.OUTER;
import static guru.qa.niffler.jupiter.annotation.User.UserType.WITH_FRIENDS;


public class FriendsWebTest extends BaseWebTest {
    private LoginPage loginPage = new LoginPage();
    private NavigationPage navigationPage = new NavigationPage();
    private FriendsPage friendsPage = new FriendsPage();

//    @BeforeEach
//    void doLogin(@User(userType = WITH_FRIENDS) UserJson userForTest) {
//        loginPage.signIn(userForTest);
//    }


    //@Test
    //кейс когда у нас два пользователя с типом друзья и проверка, что они друг у друга в друзьях
    void friendShouldBeDisplayedInTable(@User(userType = WITH_FRIENDS) UserJson anotherUserForTest) {
        friendsPage = navigationPage.goToFriends();
        friendsPage.checkingThatListExist();
        friendsPage.checkingYouFriends(anotherUserForTest.getUsername());
    }

    @ApiLogin(
            user = @GenerateUser(
                    incomeInvitations = @IncomeInvitation
            )
    )
    @GenerateUser
    @Test
    void incomeInvitationShouldBePresentInTable(@GeneratedUser(selector = NESTED) UserJson userForTest,
                                                @GeneratedUser(selector = OUTER) UserJson another) {
        open(CFG.nifflerFrontUrl() + "/main");
        friendsPage = navigationPage.goToFriends();
        //friendsPage.checkingThatListExist();
        //friendsPage.checkingYouFriends(another.getUsername());
    }


}
