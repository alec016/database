package es.degrassi.core.builder.entry;

import es.degrassi.core.builder.AbstractBuilder;
import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class EntryBuilder extends AbstractBuilder {
  protected EntryBuilder addFiled(Field field) {
    return this;
  }

  protected EntryBuilder addSubClass(Class<?> clazz) {
    return this;
  }

  public EntryBuilder addFields(Field... fields) {
    Arrays.stream(fields).forEach(this::addFiled);
    return this;
  }

  public EntryBuilder addSubClasses(Class<?>... classes) {
    Arrays.stream(classes).forEach(this::addSubClass);
    return this;
  }
}
