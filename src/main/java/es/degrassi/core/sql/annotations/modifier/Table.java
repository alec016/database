package es.degrassi.core.sql.annotations.modifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("unused")
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
@IncompatibleModifiers(modifier = { Modifier.STATIC })
public @interface Table {
  String value();
}
