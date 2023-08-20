package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.*;

public class FriendsPage {

    public FriendsPage checkingThatListExist(){
        step("Checking that the list exists", () ->
                $$(".abstract-table tbody tr").
                        shouldBe(CollectionCondition.sizeGreaterThan(0)));
        return this;
    }


    public FriendsPage checkingYouFriends(String username){
        step("Checking that you are friends", () ->
                $(byText(username)).shouldBe(visible));
        return this;
    }


    public FriendsPage checkingInvitationReceived(){
        step("Checking invitation received (page Friends)", () ->
                $("div[data-tooltip-id='submit-invitation']").shouldBe(visible));
        return this;
    }
}
