package guru.qa.niffler.api.logging;

import io.qameta.allure.attachment.AttachmentData;

public class JsonAttachment implements AttachmentData {
    private String name;
    private String json;

    public JsonAttachment(String name, String json) {
        this.name = name;
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    @Override
    public String getName() {
        return name;
    }
}
