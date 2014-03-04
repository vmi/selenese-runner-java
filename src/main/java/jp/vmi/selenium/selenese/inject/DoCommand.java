package jp.vmi.selenium.selenese.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Annotates command execution.
 * <p>
 * Expected signature: {@code Result method(Context context, ICommand command, String... curArgs)}
 * </p>
 */
@BindingAnnotation
@Target({ METHOD })
@Retention(RUNTIME)
public @interface DoCommand {
}
