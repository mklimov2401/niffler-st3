package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Allure;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    public void signIn(UserJson userForTest) {
        login(userForTest.getUsername(), userForTest.getPassword());
    }

    public void signIn(String username, String password) {
        login(username, password);
    }

    private void login(String username, String password) {
        Allure.step("Open welcome page", () -> Selenide.open("http://127.0.0.1:3000/main"));
        Allure.step("Go to login page", () -> $("a[href*='redirect']").click());
        Allure.step("Enter username", () -> $("input[name='username']").setValue(username));
        Allure.step("Enter password", () -> $("input[name='password']").setValue(password));
        Allure.step("Click submit", () -> $("button[type='submit']").click());
        Allure.step("Check open Main page", () -> $(".main-content__section-stats").should(visible));
    }
}