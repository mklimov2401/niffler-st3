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
    public ProfilePage goToProfile() {
        step("Go to profile", () -> menuElement("profile").click());
        return new ProfilePage();
    }
    public MainPage goToMain() {
        step("Go to main page", () -> menuElement("main").click());
        return new MainPage();
    }

    public LoginPage logout() {
        step("Logout", () -> menuElement("logout").click());
        return new LoginPage();
    }


    private SelenideElement menuElement(String name) {
        return $("nav").find("[data-tooltip-id='" + name + "']");
    }
}
