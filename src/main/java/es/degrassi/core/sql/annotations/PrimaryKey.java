package es.degrassi.core.sql.annotations;

import es.degrassi.core.sql.DataType;
import es.degrassi.core.sql.KeyType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@IncompatibleModifiers(modifier = { Modifier.STATIC })
public @interface PrimaryKey {
  KeyType[] keyTypes = new KeyType[] {
    KeyType.PRIMARY_KEY
  };
  DataType[] dataTypes = DataType.values();
}
