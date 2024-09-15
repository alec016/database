package es.degrassi.core.sql.annotations;

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

  private Modifier() {
  }

  public boolean isStatic() {
    return this == STATIC;
  }
}
