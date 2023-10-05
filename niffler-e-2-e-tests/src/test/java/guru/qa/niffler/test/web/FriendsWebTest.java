package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.NavigationPage;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.jupiter.annotation.GeneratedUser.Selector.NESTED;
import static guru.qa.niffler.jupiter.annotation.GeneratedUser.Selector.OUTER;
import static guru.qa.niffler.jupiter.annotation.User.UserType.WITH_FRIENDS;


public class FriendsWebTest extends BaseWebTest {
    private NavigationPage navigationPage = new NavigationPage();
    private FriendsPage friendsPage = new FriendsPage();

    private AllPeoplePage peoplePage = new AllPeoplePage();


    //@Test
    //кейс когда у нас два пользователя с типом друзья и проверка, что они друг у друга в друзьях
//    void friendShouldBeDisplayedInTable(@User(userType = WITH_FRIENDS) UserJson anotherUserForTest) {
//        friendsPage = navigationPage.goToFriends();
//        friendsPage.checkingThatListExist();
//        friendsPage.checkingYouFriends(anotherUserForTest.getUsername());
//    }

//    @ApiLogin(
//            user = @GenerateUser(
//                    friends = @Friend
//            )
//    )
//    @GenerateUser
//    @Test
//    void friendShouldBePresentInTable(@GeneratedUser(selector = NESTED) UserJson userForTest,
//                                      @GeneratedUser(selector = OUTER) UserJson another) {
//        open(CFG.nifflerFrontUrl() + "/main");
//        friendsPage = navigationPage.goToFriends();
//        friendsPage.checkingThatListExist();
//        friendsPage.checkingYouFriends(userForTest.getFriends().get(0).getUsername());
//    }

    @ApiLogin(
            user = @GenerateUser(
                   incomeInvitations = @IncomeInvitation
            )
    )
    @Test
    void incomeInvitationShouldBePresentInTable(@GeneratedUser(selector = NESTED) UserJson userForTest) {
        open(CFG.nifflerFrontUrl() + "/main");
        peoplePage = navigationPage.goToAllPeople();
        peoplePage.checkingAllPeoplePage();
        peoplePage.checkingPendingInvitation();
    }

    @ApiLogin(
            user = @GenerateUser(
                    outcomeInvitations = @OutcomeInvitation
            )
    )
    @Test
    void outcomeInvitationShouldBePresentInTable() {
        open(CFG.nifflerFrontUrl() + "/main");
        friendsPage = navigationPage.goToFriends();
        friendsPage.checkingThatListExist();
        friendsPage.checkingInvitationReceived();
    }
}
