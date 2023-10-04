package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.jupiter.annotation.GenerateUser;
import guru.qa.niffler.jupiter.annotation.IncomeInvitation;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;


public class LoginApiTest extends BaseWebTest {

    @ApiLogin(
            user = @GenerateUser
    )
    @Test
    void mainPageShouldBeVisibleAfterLogin() {
        open(CFG.nifflerFrontUrl() + "/main");
        $(".main-content__section-stats").shouldBe(visible);
        sleep(4000);
    }
}
