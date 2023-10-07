package guru.qa.niffler.config;

public interface Config {

    static Config getInstance() {
        if ("docker".equals(System.getProperty("test.env"))) {
            return DockerConfig.config;
        }
        return LocalConfig.config;
    }


    String baseUrl();

    String databaseHost();

    String nifflerFrontUrl();

    String nifflerSpendUrl();

    String nifflerAuthUrl();
    String nifflerUserDataUrl();

    String getCurrencyGrpcAddress();

    int getCurrencyGrpcPort();

    default String databaseUser() {
        return "postgres";
    }

    default String databasePassword() {
        return "secret";
    }

    default int databasePort() {
        return 5432;
    }


}
