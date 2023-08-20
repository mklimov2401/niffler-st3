package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.*;

public class NavigationPage {

    public FriendsPage goToFriends() {
        step("Go to friends", () -> menuElement("friends").click());
        return new FriendsPage();
    }

    public AllPeoplePage goToAllPeople() {
        step("Go to all people", () -> menuElement("people").click());
        return new AllPeoplePage();
    }


    private SelenideElement menuElement(String name) {
        return $("nav").find("li[data-tooltip-id='" + name + "']");
    }
}
