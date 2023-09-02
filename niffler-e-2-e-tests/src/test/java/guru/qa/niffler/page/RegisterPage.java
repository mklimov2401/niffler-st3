package guru.qa.niffler.page;


import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.step;

public class RegisterPage {

    public RegisterPage enterUsername(String username){
        step("Enter username", () -> {
            $("#username").click();
            $("#username").sendKeys(username);
        });
        return this;
    }
    public RegisterPage enterPassword(String password){
        step("Enter password", () -> {
            $("#password").click();
            $("#password").sendKeys(password);
        });
        return this;
    }
    public RegisterPage enterPasswordSubmit(String password){
        step("Enter password submit", () -> {
            $("#passwordSubmit").click();
            $("#passwordSubmit").sendKeys(password);
        });
        return this;
    }


    public RegisterPage signUp(){
        step("Sing Up", () -> $(byText("Sign Up")).click());
        return this;
    }

    public RegisterPage checkRegistered() {
        step("Check registered", ()
                -> $(byText("Congratulations! You've registered!")).shouldBe(visible));
        return this;
    }
}
