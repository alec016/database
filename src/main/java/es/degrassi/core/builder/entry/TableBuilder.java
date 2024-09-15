package es.degrassi.core.builder.entry;

import es.degrassi.core.sql.DataType;
import es.degrassi.core.sql.KeyType;
import es.degrassi.core.sql.Table;
import es.degrassi.core.sql.annotations.AutoIncrement;
import es.degrassi.core.sql.annotations.Default;
import es.degrassi.core.sql.annotations.ForeingKey;
import es.degrassi.core.sql.annotations.IncompatibleModifiers;
import es.degrassi.core.sql.annotations.NotNull;
import es.degrassi.core.sql.annotations.PrimaryKey;
import es.degrassi.core.sql.annotations.Unique;
import es.degrassi.core.sql.annotations.Unsigned;
import es.degrassi.util.InvalidDataTypeException;
import es.degrassi.util.InvalidKeyException;
import es.degrassi.util.InvalidStateException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TableBuilder extends EntryBuilder {
  private final HashMap<String, List<String>> cols = new LinkedHashMap<>();
  private String tableName;

  public static TableBuilder fromClass(Class<?> clazz) throws InvalidDataTypeException, InvalidKeyException {
    TableBuilder builder = new TableBuilder(clazz.getSimpleName());
    builder
      .addFields(Arrays
        .stream(clazz.getDeclaredFields())
        .filter(field -> Arrays
          .stream(field.getAnnotations())
          .anyMatch(annotation -> Arrays
            .stream(annotation.getClass().getAnnotations())
            .filter(ann -> ann instanceof IncompatibleModifiers)
            .map(ann -> (IncompatibleModifiers) ann)
            .map(IncompatibleModifiers::modifier)
            .allMatch(modifiers -> {
              boolean accepts = true;
              System.out.println("modifiers of " + field.getName() + ": " + Arrays.stream(modifiers).map(Enum::name).toList());
              for (es.degrassi.core.sql.annotations.Modifier modifier : modifiers) {
                if (!accepts) return false;
                switch (modifier) {
                  case FINAL -> accepts = !Modifier.isFinal(field.getModifiers());
                  case PUBLIC -> accepts = !Modifier.isPublic(field.getModifiers());
                  case NATIVE -> accepts = !Modifier.isNative(field.getModifiers());
                  case STATIC -> accepts = !Modifier.isStatic(field.getModifiers());
                  case DEFAULT, ABSTRACT -> accepts = !Modifier.isAbstract(field.getModifiers());
                  case PRIVATE -> accepts = !Modifier.isPrivate(field.getModifiers());
                  case PROTECTED -> accepts = !Modifier.isProtected(field.getModifiers());
                  case TRANSIENT -> accepts = !Modifier.isTransient(field.getModifiers());
                  case VOLATILE -> accepts = !Modifier.isVolatile(field.getModifiers());
                  case SYNCHRONIZED -> accepts = !Modifier.isSynchronized(field.getModifiers());
                  case STRICTFP -> accepts = !Modifier.isStrict(field.getModifiers());
                }
              }
              return accepts;
            })
          )
        ).toArray(Field[]::new)
      );
    return builder;
  }

  public static TableBuilder empty(String tableName) {
    return new TableBuilder(tableName);
  }

  public static TableBuilder empty() {
    return new TableBuilder("");
  }

  private TableBuilder(String tableName) {
    this.tableName = tableName;
  }

  public TableBuilder setTableName(String name) {
    this.tableName = name;
    return this;
  }

  public TableBuilder addField(String columnName, List<String> data) {
    cols.put(columnName, data);
    return this;
  }

  public TableBuilder addField(String columnName, DataType dataType, KeyType... modifiers) {
    List<String> list = new LinkedList<>();
    list.add(dataType.name());
    cols.put(columnName, list);
    Arrays.stream(modifiers).map(KeyType::getName).forEach(cols.get(columnName)::add);
    return this;
  }

  public TableBuilder addField(Field field) throws InvalidDataTypeException, InvalidKeyException {
    List<String> modifiers = new LinkedList<>();
    DataType dataType = DataType.fromClass(field.getType());
    if (dataType == null) return this;
    if (dataType.isVarchar())
      modifiers.add(dataType.name() + "(100)");
    else if (dataType.isEnum() && field.getType().isEnum()) {
      String values = Arrays
        .stream((Enum<?>[]) field.getType().getEnumConstants())
        .map(Enum::name)
        .map(name -> "\"" + name + "\"")
        .toList()
        .toString()
        .replaceAll("[\\[\\]]", "");
      modifiers.add(dataType.name() + "(" + values + ")");
    } else
      modifiers.add(dataType.name());
    for (Annotation annotation : field.getAnnotations()) {
      if (annotation instanceof AutoIncrement) {
        if (Arrays.stream(AutoIncrement.dataTypes).noneMatch(type -> DataType.fromClass(field.getType()).equals(type)))
          throw new InvalidDataTypeException(field.getType(), Number.class);
        modifiers.addAll(modifiers(AutoIncrement.keyTypes));
      } else if (annotation instanceof PrimaryKey) {
        if (modifiers.stream().anyMatch(value -> value.contains(KeyType.PRIMARY_KEY.getName())))
          throw new InvalidKeyException(false, KeyType.PRIMARY_KEY);
        modifiers.addAll(modifiers(PrimaryKey.keyTypes));
      } else if (annotation instanceof NotNull) {
        modifiers.addAll(modifiers(NotNull.keyTypes));
      } else if (annotation instanceof Unique) {
        modifiers.addAll(modifiers(Unique.keyTypes));
      } else if (annotation instanceof Unsigned) {
        if (Arrays.stream(Unsigned.dataTypes).noneMatch(type -> DataType.fromClass(field.getType()).equals(type)))
          throw new InvalidDataTypeException(field.getType(), Number.class);
        modifiers.addAll(modifiers(Unsigned.keyTypes));
      } else if (annotation instanceof ForeingKey fk) {
        modifiers.addAll(modifiers(ForeingKey.keyTypes));
        modifiers.add(fk.table() + "(" + fk.columnName() + ")");
      } else if (annotation instanceof Default df) {
        modifiers.addAll(modifiers(Default.keyTypes));
        if (dataType.isCompatible(String.class) || field.getType().isEnum()) {
          modifiers.add("\"" + df.value() + "\"");
        } else {
          modifiers.add(df.value());
        }
      }
    }
    cols.put(field.getName(), modifiers);
    return this;
  }

  private List<String> modifiers(KeyType... keyTypes) {
    List<String> list = new LinkedList<>();
    for (KeyType type : keyTypes)
      list.add(type.getName());
    return list;
  }

  private TableBuilder unsigned(String field) throws InvalidStateException{
    if (cols.get(field).isEmpty()) throw new InvalidStateException("Can not set default value to an empty column definition");
    cols.get(field).add(KeyType.UNSIGNED.getName());
    return this;
  }

  public <T> TableBuilder defaultValue(String field, T value) throws InvalidStateException {
    if (cols.get(field).isEmpty()) throw new InvalidStateException("Can not set default value to an empty column definition");
    AtomicReference<DataType> type = new AtomicReference<>(null);
    cols.get(field).forEach(modifier -> {
      if (type.get() != null) return;
      if (DataType.from(modifier) != null) type.set(DataType.from(modifier));
    });
    if (!type.get().isCompatible(value.getClass())) throw new InvalidStateException("Invalid types found, can not assign default value " + value + " with type: \"" + value.getClass().getSimpleName() + "\" to a type of \"" + type.get().name() + "\"");
    if (value instanceof String) {
      cols.get(field).add(KeyType.DEFAULT.getName() + " \"" + value + "\"");
    } else if (value instanceof Character) {
      cols.get(field).add(KeyType.DEFAULT.getName() + " \"" + value + "\"");
    } else {
      cols.get(field).add(KeyType.DEFAULT.getName() + " " + value);
    }
    return this;
  }

  public TableBuilder asPrimaryKey(String col) throws InvalidStateException {
    if (!cols.containsKey(col)) throw new InvalidStateException("Can not set Primary Key to a non existing column");
    cols.get(col).add(KeyType.PRIMARY_KEY.getName());
    return this;
  }

  public TableBuilder notNull(String col) throws InvalidStateException {
    if (!cols.containsKey(col)) throw new InvalidStateException("Can not set Not Null to a non existing column");
    cols.get(col).add(KeyType.NOT_NULL.getName());
    return this;
  }

  public TableBuilder autoincrement(String col) throws InvalidStateException {
    if (!cols.containsKey(col)) throw new InvalidStateException("Can not set Autoincrement to a non existing column");
    if (!containsDataType(col, DataType.INT, DataType.DECIMAL, DataType.FLOAT, DataType.LONGDECIMAL,
      DataType.LONGINT, DataType.MEDIUMDECIMAL, DataType.MEDIUMINT, DataType.SMALLINT, DataType.TINYDECIMAL,
      DataType.TINYINT)) throw new InvalidStateException("Can not set Autoincrement to a non numerical column");
    cols.get(col).add(KeyType.AUTOINCREMENT.getName());
    return this;
  }

  public TableBuilder foreingKey(String col, String tableReference, String colReference) throws InvalidStateException {
    if (tableReference.isEmpty() || colReference.isEmpty()) throw new InvalidStateException("Can not reference to an empty tableName or an empty columnName");
    cols.get(col).addAll(List.of(
      KeyType.FOREING_KEY.getName(),
      KeyType.REFERENCES.getName(),
      tableReference + "(" + colReference + ")"
    ));
    return this;
  }

  private boolean containsDataType(String col, DataType... types) {
    AtomicBoolean contains = new AtomicBoolean(false);
    Arrays.stream(types).map(DataType::name).forEach(type -> {
      if (cols.get(col).contains(type)) contains.set(true);
    });
    return contains.get();
  }

  public Table build() throws InvalidStateException {
    if (tableName == null || tableName.isEmpty()) throw new InvalidStateException("Table name can not be null or empty");
    if (cols.isEmpty()) throw new InvalidStateException("Columns can not be empty");
    if (cols.values().stream().noneMatch(value -> value.contains(KeyType.PRIMARY_KEY.getName()))) throw new InvalidStateException("One column must be Primary Key");
    if (cols.values().stream().filter(value -> value.contains(KeyType.PRIMARY_KEY.getName())).toList().size() > 1) throw new InvalidStateException("Only one column can be Primary Key");
    return new Table(cols, tableName);
  }
}
