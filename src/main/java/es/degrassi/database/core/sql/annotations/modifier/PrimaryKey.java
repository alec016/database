package es.degrassi.database.core.sql.annotations.modifier;

import es.degrassi.database.core.sql.DataType;
import es.degrassi.database.core.sql.KeyType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("unused")
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
