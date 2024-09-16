package es.degrassi.core.sql.annotations;

import es.degrassi.core.sql.DataType;
import es.degrassi.core.sql.KeyType;
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
public @interface AutoIncrement {
  KeyType[] keyTypes = new KeyType[] {
    KeyType.AUTOINCREMENT
  };
  DataType[] dataTypes = new DataType[] {
    DataType.INT,
    DataType.SMALLINT,
    DataType.LONGDECIMAL,
    DataType.MEDIUMDECIMAL,
    DataType.TINYDECIMAL,
    DataType.LONGINT,
    DataType.MEDIUMINT,
    DataType.TINYINT,
    DataType.DECIMAL,
    DataType.FLOAT
  };
}
