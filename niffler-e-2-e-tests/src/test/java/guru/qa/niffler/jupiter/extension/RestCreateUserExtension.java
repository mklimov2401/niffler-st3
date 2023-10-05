package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.GenerateUser;
import guru.qa.niffler.model.UserJson;

import java.util.List;

public class RestCreateUserExtension extends CreateUserExtension{
    @Override
    protected UserJson createUserForTest(GenerateUser annotation) {
        return null;
    }

    @Override
    protected List<UserJson> createFriendsIfPresent(GenerateUser annotation, UserJson currentUser) {
        return null;
    }

    @Override
    protected List<UserJson> createIncomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) {
        return null;
    }

    @Override
    protected List<UserJson> createOutcomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) {
        return null;
    }
}
