package guru.qa.niffler.api.logging;

import io.qameta.allure.attachment.DefaultAttachmentProcessor;
import io.qameta.allure.attachment.FreemarkerAttachmentRenderer;

public class AllureJsonLogger {
    public static void json(String name, String json){
        JsonAttachment jsonAttachment = new JsonAttachment(name, json);
        new DefaultAttachmentProcessor().addAttachment(jsonAttachment,
                new FreemarkerAttachmentRenderer("json.ftl"));
    }
}
