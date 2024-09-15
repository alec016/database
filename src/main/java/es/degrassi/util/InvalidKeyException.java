package es.degrassi.util;

import es.degrassi.core.sql.KeyType;

public class InvalidKeyException extends Exception {
  public InvalidKeyException(boolean accepts, KeyType type) {
    super(
      accepts
        ? "Invalid Key found: " + type.name()
        : "Invalid Key found: " + type.name() + " can not have more than one"
    );
  }
}
