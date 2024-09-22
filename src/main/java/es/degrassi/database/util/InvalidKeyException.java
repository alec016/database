package es.degrassi.database.util;

import es.degrassi.database.core.sql.KeyType;

@SuppressWarnings("unused")
public class InvalidKeyException extends Exception {
  public InvalidKeyException(boolean accepts, KeyType type) {
    super(
      accepts
        ? "Invalid Key found: " + type.name()
        : "Invalid Key found: " + type.name() + " can not have more than one"
    );
  }
}
