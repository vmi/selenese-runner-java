package jp.vmi.selenium.selenese;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@BindingAnnotation
@Target({ METHOD })
@Retention(RUNTIME)
public @interface RunFiles {
}
