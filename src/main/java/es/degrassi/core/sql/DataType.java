package es.degrassi.core.sql;

import java.sql.Date;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public enum DataType {
  VARCHAR(String.class),
  INT(Integer.class, int.class),
  DECIMAL(Double.class, double.class),
  DATE(Date.class),
  TIMESTAMP(Date.class),
  TINYINT(Short.class, short.class),
  MEDIUMINT(Integer.class, int.class),
  LONGINT(Long.class, long.class),
  TINYDECIMAL(Double.class, double.class),
  MEDIUMDECIMAL(Double.class, double.class),
  LONGDECIMAL(Double.class, double.class),
  BLOB(Class.class), // see what class fits better
  SMALLINT(Short.class, short.class),
  DATETIME(Date.class),
  CHAR(Character.class, char.class),
  TEXT(String.class),
  YEAR(Class.class), // see what class fits better
  ENUM(Enum.class),
  SET(Set.class),
  GEOMETRY(Class.class), // see what class fits better
  FLOAT(Float.class, float.class),
  BOOLEAN(Boolean.class, boolean.class);

  private final Class<?>[] classReference;
  DataType(Class<?>... classReference) {
    this.classReference = classReference;
  }

  public static DataType fromClass(Class<?> clazz) {
    AtomicReference<DataType> type = new AtomicReference<>(null);
    if (clazz.isEnum()) return ENUM;
    Arrays.stream(values()).forEach(t -> {
      if(type.get() != null) return;
      if (clazz != Class.class && t.isCompatible(clazz))
        type.set(t);
    });
    return type.get();
  }

  public static DataType from(String dataType) {
    if (dataType.toUpperCase(Locale.ROOT).startsWith("VARCHAR")) return VARCHAR;
    return switch(dataType.toUpperCase(Locale.ROOT)) {
      case "INT" -> INT;
      case "DECIMAL" -> DECIMAL;
      case "DATE" -> DATE;
      case "TIMESTAMP" -> TIMESTAMP;
      case "TINYINT" -> TINYINT;
      case "MEDIUMINT" -> MEDIUMINT;
      case "LONGINT" -> LONGINT;
      case "TINYDECIMAL" -> TINYDECIMAL;
      case "MEDIUMDECIMAL" -> MEDIUMDECIMAL;
      case "LONGDECIMAL" -> LONGDECIMAL;
      case "SMALLINT" -> SMALLINT;
      case "DATETIME" -> DATETIME;
      case "CHAR" -> CHAR;
      case "TEXT" -> TEXT;
      case "ENUM" -> ENUM;
      case "SET" -> SET;
      case "FLOAT" -> FLOAT;
      case "BOOLEAN" -> BOOLEAN;
      case "BLOB" -> BLOB; // see what class fits better
      case "YEAR" -> YEAR; // see what class fits better
      case "GEOMETRY" -> GEOMETRY; // see what class fits better
      default -> null;
    };
  }

  public boolean isCompatible(Class<?> clazz) {
    if (this == ENUM && clazz.isEnum()) return true;
    return Arrays.stream(classReference).anyMatch(c -> c == clazz);
  }

  @Override
  public String toString() {
    return "DataType{" +
      "name=" + name() +
      ", classReference=" + Arrays.stream(classReference).map(Class::getName).toList() +
      '}';
  }

  public boolean isVarchar() {
    return this == VARCHAR;
  }

  public boolean isEnum() {
    return this == ENUM;
  }
}
