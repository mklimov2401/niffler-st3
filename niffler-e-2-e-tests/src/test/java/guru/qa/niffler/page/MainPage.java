package guru.qa.niffler.page;

import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Allure;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {

    public MainPage findSpend(SpendJson spend){
        Allure.step("Find spend", () ->
                $(".spendings__content tbody")
                        .$$("tr")
                        .find(text(spend.getDescription()))
                        .$$("td")
                        .first()
                        .scrollTo()
                        .click());
        return this;
    }

    public MainPage deleteSpend(){
        Allure.step("Delete spend", () ->
                $(byText("Delete selected")).click());
        return this;
    }

    public MainPage checkDeleteSpend(){
        Allure.step("Checking delete spend", () ->
                $(".spendings__content tbody")
                        .$$("tr")
                        .shouldHave(size(0)));
        return this;
    }
}
