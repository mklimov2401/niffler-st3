package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.config.Config;
import org.junit.jupiter.api.Assertions;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FriendsPage extends BasePage {

    public FriendsPage checkingThatListExist() {
        step("Checking that the list exists", () ->
                $$(".abstract-table tbody tr").
                        shouldBe(CollectionCondition.sizeGreaterThan(0)));
        return this;
    }


    public FriendsPage checkingYouFriends(String username) {
        step("Checking that you are friends", () ->
                $(byText(username)).shouldBe(visible));
        return this;
    }


    public FriendsPage checkingInvitationReceived() {
        step("Checking invitation received (page Friends)", () ->
                $("div[data-tooltip-id='submit-invitation']").shouldBe(visible));
        return this;
    }

    public FriendsPage checkingFreindsPage() {
        step("Check friends page", () ->
                assertEquals(WebDriverRunner.url(), cfg.baseUrl() + "/friends"));
        return this;
    }
}
