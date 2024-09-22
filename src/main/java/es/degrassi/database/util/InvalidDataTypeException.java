package es.degrassi.database.util;

@SuppressWarnings("unused")
public class InvalidDataTypeException extends Exception {
  public InvalidDataTypeException(Class<?> expected, Class<?> found) {
    super("Invalid data type. Found: " + found.getSimpleName() + " and expected " + expected.getSimpleName());
  }

  public InvalidDataTypeException(String message) {
    super(message);
  }
}
