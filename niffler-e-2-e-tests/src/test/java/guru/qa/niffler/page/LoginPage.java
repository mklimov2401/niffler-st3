package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.*;

public class LoginPage extends BasePage{


    public void checkWelcome() {
        step("check welcome", () ->
                $(byText("Welcome to magic journey with Niffler. The coin keeper")).shouldBe(visible));
    }

    public void signIn(UserJson userForTest) {
        login(userForTest.getUsername(), userForTest.getPassword());
    }

    @Step("Authorization")
    public MainPage signIn(String username, String password) {
        login(username, password);
        return new MainPage();
    }
    @Step("Open main page")
    public MainPage openMain() {
        open(CFG.nifflerFrontUrl() + "/main");
        return new MainPage();
    }


    public void checkLoginError() {
        step("Check login error", () ->
                $(byText("Неверные учетные данные пользователя")).shouldBe(visible));
    }

    public RegisterPage toRegister() {
        openWelcomePage();
        step("Go to register", () ->
                $(byText("Register")).click());
        return new RegisterPage();
    }

    private void openWelcomePage(){
        step("Open welcome page", () -> Selenide.open(CFG.baseUrl() + "/main"));
    }

    private void login(String username, String password) {
        openWelcomePage();
        step("Go to login page", () -> $("a[href*='redirect']").click());
        step("Enter username", () -> $("input[name='username']").setValue(username));
        step("Enter password", () -> $("input[name='password']").setValue(password));
        step("Click submit", () -> $("button[type='submit']").click());
    }
}