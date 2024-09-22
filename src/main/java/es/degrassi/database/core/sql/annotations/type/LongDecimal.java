package es.degrassi.database.core.sql.annotations.type;

import es.degrassi.database.core.sql.annotations.modifier.IncompatibleModifiers;
import es.degrassi.database.core.sql.annotations.modifier.Modifier;
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
public @interface LongDecimal {
  String toString = "LONGDECIMAL";
}
