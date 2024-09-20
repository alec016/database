package es.degrassi.core.sql.annotations.type;

public interface ColumnType {
  static boolean isValid(Class<?> clazz) {
    if (!clazz.isAnnotation()) return false;
    return clazz.isAssignableFrom(Boolean.class) ||
      clazz.isAssignableFrom(Char.class) ||
      clazz.isAssignableFrom(Date.class) ||
      clazz.isAssignableFrom(DateTime.class) ||
      clazz.isAssignableFrom(Decimal.class) ||
      clazz.isAssignableFrom(Enum.class) ||
      clazz.isAssignableFrom(Float.class) ||
      clazz.isAssignableFrom(Int.class) ||
      clazz.isAssignableFrom(LongDecimal.class) ||
      clazz.isAssignableFrom(LongInt.class) ||
      clazz.isAssignableFrom(MediumDecimal.class) ||
      clazz.isAssignableFrom(MediumInt.class) ||
      clazz.isAssignableFrom(Set.class) ||
      clazz.isAssignableFrom(SmallInt.class) ||
      clazz.isAssignableFrom(Text.class) ||
      clazz.isAssignableFrom(Timestamp.class) ||
      clazz.isAssignableFrom(TinyDecimal.class) ||
      clazz.isAssignableFrom(TinyInt.class) ||
      clazz.isAssignableFrom(Varchar.class);
  }

  static boolean mustBeQuotated(Class<?> clazz) {
    if (!clazz.isAnnotation()) return false;
    return clazz.isAssignableFrom(Char.class) ||
      clazz.isAssignableFrom(Set.class) ||
      clazz.isAssignableFrom(Text.class) ||
      clazz.isAssignableFrom(Enum.class) ||
      clazz.isAssignableFrom(Varchar.class);
  }
}
