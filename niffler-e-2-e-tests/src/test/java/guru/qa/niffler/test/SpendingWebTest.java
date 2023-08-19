package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpendingWebTest extends BaseWebTest {

    private final String USERNAME = "dima";
    private final String PASSWORD = "123456";
    private final String CATEGORY = "Рыбалка";
    private final String DESCRIPTION = "Рыбалка на Ладоге";
    private final double AMOUNT = 14000.00;


    @BeforeEach
    void doLogin() {
        new LoginPage().signIn(USERNAME, PASSWORD);
    }

    @Category(
            username = USERNAME,
            category = CATEGORY
    )
    @Spend(
            username = USERNAME,
            description = DESCRIPTION,
            category = CATEGORY,
            amount = AMOUNT,
            currency = CurrencyValues.RUB
    )
    @Test
    void spendingShouldBeDeletedAfterDeleteAction(SpendJson createdSpend) {
        new MainPage()
                .findSpend(createdSpend)
                .deleteSpend()
                .checkDeleteSpend();
    }
}
