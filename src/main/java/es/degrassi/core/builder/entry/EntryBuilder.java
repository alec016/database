package es.degrassi.core.builder.entry;

import es.degrassi.core.builder.AbstractBuilder;
import es.degrassi.util.InvalidDataTypeException;
import es.degrassi.util.InvalidKeyException;
import java.lang.reflect.Field;
import java.util.Arrays;

@SuppressWarnings("unused")
public abstract class EntryBuilder extends AbstractBuilder {
  protected EntryBuilder addField(Field field) throws InvalidDataTypeException, InvalidKeyException {
    return this;
  }

  protected EntryBuilder addSubClass(Class<?> clazz) {
    return this;
  }

  public EntryBuilder addFields(Field... fields) throws InvalidDataTypeException, InvalidKeyException {
    for (Field field : fields) {
      addField(field);
    }
    return this;
  }

  public EntryBuilder addSubClasses(Class<?>... classes) {
    Arrays.stream(classes).forEach(this::addSubClass);
    return this;
  }
}
