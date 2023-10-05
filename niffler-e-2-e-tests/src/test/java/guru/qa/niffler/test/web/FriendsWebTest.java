package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.NavigationPage;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.jupiter.annotation.GeneratedUser.Selector.NESTED;
import static guru.qa.niffler.jupiter.annotation.GeneratedUser.Selector.OUTER;


public class FriendsWebTest extends BaseWebTest {
    private NavigationPage navigationPage = new NavigationPage();
    private FriendsPage friendsPage = new FriendsPage();

    private AllPeoplePage peoplePage = new AllPeoplePage();

    @ApiLogin(
            user = @GenerateUser(
                    friends = @Friend
            )
    )
    @GenerateUser
    @Test
    void friendShouldBePresentInTable(@GeneratedUser(selector = NESTED) UserJson userForTest,
                                      @GeneratedUser(selector = OUTER) UserJson another) {
        open(CFG.nifflerFrontUrl() + "/main");
        friendsPage = navigationPage.goToFriends();
        friendsPage.checkingThatListExist();
        friendsPage.checkingYouFriends(userForTest.getFriends().get(0).getUsername());
    }

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
