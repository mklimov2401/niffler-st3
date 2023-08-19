package guru.qa.niffler.page;

import io.qameta.allure.Allure;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class AllPeoplePage {

    public AllPeoplePage checkingDisplayedSubmitInvitation(){
        Allure.step("Checking display submit-invitation submit (page \"All people\")", () ->
                $("div[data-tooltip-id='submit-invitation']").shouldBe(exist, visible));
        return this;
    }

    public AllPeoplePage checkingDisplayedSubmitDeclined(){
        Allure.step("Checking display decline-invitation submit (page \"All people\")", () ->
                $("div[data-tooltip-id='decline-invitation']").shouldBe(exist, visible));
        return this;
    }

    public AllPeoplePage checkingPendingInvitation(){
        Allure.step("Checking pending invitation", () ->
                $(byText("Pending invitation")).shouldBe(visible));
        return this;
    }


}
