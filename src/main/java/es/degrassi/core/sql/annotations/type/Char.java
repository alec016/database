package es.degrassi.core.sql.annotations.type;

import es.degrassi.core.sql.annotations.modifier.IncompatibleModifiers;
import es.degrassi.core.sql.annotations.modifier.Modifier;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("unused")
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.RECORD_COMPONENT})
@IncompatibleModifiers(modifier = { Modifier.STATIC })
public @interface Char {
  String toString = "CHAR";
}
