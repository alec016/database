package es.degrassi.database.core.sql.annotations.modifier;

public enum Modifier {
  PUBLIC,
  PROTECTED,
  PRIVATE,
  ABSTRACT,
  DEFAULT,
  STATIC,
  FINAL,
  TRANSIENT,
  VOLATILE,
  SYNCHRONIZED,
  NATIVE,
  STRICTFP;

  Modifier() {
  }

  public boolean isStatic() {
    return this == STATIC;
  }
}
