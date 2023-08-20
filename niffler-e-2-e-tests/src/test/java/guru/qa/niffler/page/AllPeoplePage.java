package guru.qa.niffler.page;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.*;

public class AllPeoplePage {

    public AllPeoplePage checkingDisplayedSubmitInvitation(){
        step("Checking display submit-invitation submit (page \"All people\")", () ->
                $("div[data-tooltip-id='submit-invitation']").shouldBe(visible));
        return this;
    }

    public AllPeoplePage checkingDisplayedSubmitDeclined(){
        step("Checking display decline-invitation submit (page \"All people\")", () ->
                $("div[data-tooltip-id='decline-invitation']").shouldBe(visible));
        return this;
    }

    public AllPeoplePage checkingPendingInvitation(){
        step("Checking pending invitation", () ->
                $(byText("Pending invitation")).shouldBe(visible));
        return this;
    }


}
