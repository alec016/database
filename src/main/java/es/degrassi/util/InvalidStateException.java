package es.degrassi.util;

public class InvalidStateException extends Exception {
  public InvalidStateException() {
    super("Invalid state");
  }

  public InvalidStateException(String message) {
    super(message);
  }
}
