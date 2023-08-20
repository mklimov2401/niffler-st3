package guru.qa.niffler.page;

import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Allure;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.*;

public class MainPage {

    public MainPage findSpend(SpendJson spend){
        step("Find spend", () ->
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
        step("Delete spend", () ->
                $(byText("Delete selected")).click());
        return this;
    }

    public MainPage checkDeleteSpend(){
        step("Checking delete spend", () ->
                $(".spendings__content tbody")
                        .$$("tr")
                        .shouldHave(size(0)));
        return this;
    }
}
