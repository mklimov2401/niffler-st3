package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.model.UserJson;

import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.*;

public class LoginPage {
    public void signIn(UserJson userForTest) {
        login(userForTest.getUsername(), userForTest.getPassword());
    }

    public void signIn(String username, String password) {
        login(username, password);
    }

    private void login(String username, String password) {
        step("Open welcome page", () -> Selenide.open("http://127.0.0.1:3000/main"));
        step("Go to login page", () -> $("a[href*='redirect']").click());
        step("Enter username", () -> $("input[name='username']").setValue(username));
        step("Enter password", () -> $("input[name='password']").setValue(password));
        step("Click submit", () -> $("button[type='submit']").click());
    }
}
