package guru.qa.niffler.page;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllPeoplePage extends BasePage {

    public AllPeoplePage checkingDisplayedSubmitInvitation() {
        step("Checking display submit-invitation submit (page \"All people\")", () ->
                $("div[data-tooltip-id='submit-invitation']").shouldBe(visible));
        return this;
    }

    public AllPeoplePage checkingDisplayedSubmitDeclined() {
        step("Checking display decline-invitation submit (page \"All people\")", () ->
                $("div[data-tooltip-id='decline-invitation']").shouldBe(visible));
        return this;
    }

    public AllPeoplePage checkingPendingInvitation() {
        step("Checking pending invitation", () ->
                $(byText("Pending invitation")).shouldBe(visible));
        return this;
    }


    public AllPeoplePage checkingAllPeoplePage() {
        step("Check all people page", () ->
                assertEquals(WebDriverRunner.url(), cfg.baseUrl() + "/people"));
        return this;
    }
}
