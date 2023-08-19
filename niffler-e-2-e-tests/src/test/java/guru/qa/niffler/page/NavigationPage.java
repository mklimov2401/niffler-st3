package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Allure;

import static com.codeborne.selenide.Selenide.$;

public class NavigationPage {

    public FriendsPage goToFriends() {
        Allure.step("Go to friends", () -> menuElement("friends").click());
        return new FriendsPage();
    }

    public AllPeoplePage goToAllPeople() {
        Allure.step("Go to all people", () -> menuElement("people").click());
        return new AllPeoplePage();
    }

    public NavigationPage currentUserDisplayInReport(UserJson user) {
        Allure.step("Username: " + user.getUsername());
        return this;
    }

    private SelenideElement menuElement(String name) {
        return $("nav").find("li[data-tooltip-id='" + name + "']");
    }
}
