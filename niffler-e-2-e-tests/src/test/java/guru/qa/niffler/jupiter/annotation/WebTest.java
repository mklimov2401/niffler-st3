package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({DbUserExtension.class,
        RestCreateUserExtension.class,
        //DbCreateUserExtension.class,
        ApiLoginExtension.class,
        BrowserExtension.class,
        JpaExtension.class})
public @interface WebTest {
}
