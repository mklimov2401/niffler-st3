package guru.qa.niffler.page;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.db.model.CurrencyValues;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfilePage extends BasePage{

    SelenideElement categoryEl = $("input[name='category']");
    SelenideElement nameEl = $("input[name='firstname']");
    SelenideElement surnameEl = $("input[name='surname']");

    public ProfilePage enterCategory(String category) {
        step("Enter category - " + category,
                () -> {
                    categoryEl.click();
                    categoryEl.sendKeys(category);
                });
        return this;
    }

    public ProfilePage create() {
        step("Click create", () -> $(byText("Create")).click());
        return this;
    }

    public ProfilePage checkAddedCategory(String category) {
        step("Check category",
                () -> $("li.categories__item").shouldBe(visible).shouldBe(text(category)));
        return this;
    }

    public ProfilePage enterName(String name) {
        step("Enter name - " + name,
                () -> {
                    nameEl.scrollTo();
                    nameEl.shouldBe(visible).click();
                    nameEl.shouldBe(visible).sendKeys(name);
                });
        return this;
    }

    public ProfilePage enterSurname(String surname) {
        step("Enter surname - " + surname,
                () -> {
                    surnameEl.shouldBe(visible).click();
                    surnameEl.shouldBe(visible).sendKeys(surname);
                });
        return this;
    }

    public ProfilePage submit() {
        step("Click submit",
                () -> $(byText("Submit")).click());
        return this;
    }

    public void checkEnterProfile(String name, String surname) {
        step("Check enter info in profile",
                () -> Assertions.assertAll(() -> {
                    nameEl.scrollTo();
                    nameEl.shouldBe(visible).shouldBe(value(name));
                    surnameEl.shouldBe(visible).shouldBe(value(surname));
                }));
    }

    public ProfilePage changeCurrency(CurrencyValues currencyValues) {
        step("Change currency",
                () -> {
                    $("div.select-wrapper").scrollTo();
                    $("div.select-wrapper").shouldBe(visible).click();
                    $(byText(currencyValues.name())).shouldBe(visible).click();
                });
        return this;
    }

    public void checkCurrency(CurrencyValues currencyValues) {
        step("Check currency",
                () -> {
                    nameEl.scrollTo();
                    $(byText(currencyValues.name())).shouldBe(visible);
                });
    }

    public ProfilePage checkingProfilePage() {
        step("Check profile page", () ->
                assertEquals(WebDriverRunner.url(), cfg.baseUrl() + "/profile"));
        return this;
    }
}
