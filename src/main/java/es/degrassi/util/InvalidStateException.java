package es.degrassi.util;

@SuppressWarnings("unused")
public class InvalidStateException extends Exception {
  public InvalidStateException() {
    super("Invalid state");
  }

  public InvalidStateException(String message) {
    super(message);
  }
}
