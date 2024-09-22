package es.degrassi.database.util;

@SuppressWarnings("unused")
public class InvalidStateException extends Exception {
  public InvalidStateException() {
    super("Invalid state");
  }

  public InvalidStateException(String message) {
    super(message);
  }
}
