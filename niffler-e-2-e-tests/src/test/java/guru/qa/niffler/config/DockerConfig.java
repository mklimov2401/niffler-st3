package guru.qa.niffler.config;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;

public class DockerConfig implements Config {

    static final DockerConfig config = new DockerConfig();

    static {
        Configuration.browserSize = "1920x1200";
        Configuration.remote = "http://selenoid:4444/wd/hub";
        Configuration.timeout = 10000;
        Configuration.browser = "chrome";
        Configuration.browserVersion = "110.0";
        Configuration.browserCapabilities = new ChromeOptions().addArguments("--no-sandbox");
    }

    private DockerConfig() {
    }

    @Override
    public String baseUrl() {
        return "http://frontend.niffler.dc";
    }

    @Override
    public String databaseHost() {
        return "niffler-all-db";
    }

    @Override
    public String nifflerFrontUrl() {
        return "http://frontend.niffler.dc";
    }


    @Override
    public String nifflerSpendUrl() {
        return "http://spend.niffler.dc:8093/";
    }


    @Override
    public String nifflerAuthUrl() {
        return "http://auth.niffler.dc:9000";
    }

    @Override
    public String nifflerUserDataUrl() {
        return "http://userdata.niffler.dc:8089/";
    }


    @Override
    public String getCurrencyGrpcAddress() {
        return "currency.niffler.dc";
    }

    @Override
    public int getCurrencyGrpcPort() {
        return 8092;
    }
}
