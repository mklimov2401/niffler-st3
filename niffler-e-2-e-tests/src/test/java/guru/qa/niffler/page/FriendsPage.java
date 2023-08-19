package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import io.qameta.allure.Allure;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {

    public FriendsPage checkingThatListExist(){
        Allure.step("Checking that the list exists", () ->
                $$(".abstract-table tbody tr").
                        shouldBe(CollectionCondition.sizeGreaterThan(0)));
        return this;
    }


    public FriendsPage checkingYouFriends(){
        Allure.step("Checking that you are friends", () ->
                $(byText("You are friends")).shouldBe(exist, visible));
        return this;
    }


    public FriendsPage checkingInvitationReceived(){
        Allure.step("Checking invitation received (page Friends)", () ->
                $("div[data-tooltip-id='submit-invitation']").shouldBe(exist, visible));
        return this;
    }
}
