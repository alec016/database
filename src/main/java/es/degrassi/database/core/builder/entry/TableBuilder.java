package es.degrassi.database.core.builder.entry;

import es.degrassi.database.core.sql.DataType;
import es.degrassi.database.core.sql.KeyType;
import es.degrassi.database.core.sql.Table;
import es.degrassi.database.core.sql.annotations.modifier.AutoIncrement;
import es.degrassi.database.core.sql.annotations.modifier.Column;
import es.degrassi.database.core.sql.annotations.modifier.Default;
import es.degrassi.database.core.sql.annotations.modifier.ForeingKey;
import es.degrassi.database.core.sql.annotations.modifier.IncompatibleModifiers;
import es.degrassi.database.core.sql.annotations.modifier.NotNull;
import es.degrassi.database.core.sql.annotations.modifier.PrimaryKey;
import es.degrassi.database.core.sql.annotations.modifier.Unique;
import es.degrassi.database.core.sql.annotations.type.Boolean;
import es.degrassi.database.core.sql.annotations.type.Char;
import es.degrassi.database.core.sql.annotations.type.ColumnType;
import es.degrassi.database.core.sql.annotations.type.Date;
import es.degrassi.database.core.sql.annotations.type.DateTime;
import es.degrassi.database.core.sql.annotations.type.Decimal;
import es.degrassi.database.core.sql.annotations.type.Enum;
import es.degrassi.database.core.sql.annotations.type.Float;
import es.degrassi.database.core.sql.annotations.type.Int;
import es.degrassi.database.core.sql.annotations.type.LongDecimal;
import es.degrassi.database.core.sql.annotations.type.LongInt;
import es.degrassi.database.core.sql.annotations.type.MediumDecimal;
import es.degrassi.database.core.sql.annotations.type.MediumInt;
import es.degrassi.database.core.sql.annotations.type.Set;
import es.degrassi.database.core.sql.annotations.type.Timestamp;
import es.degrassi.database.core.sql.annotations.type.TinyDecimal;
import es.degrassi.database.core.sql.annotations.type.TinyInt;
import es.degrassi.database.core.sql.annotations.type.Varchar;
import es.degrassi.database.util.InvalidDataTypeException;
import es.degrassi.database.util.InvalidKeyException;
import es.degrassi.database.util.InvalidStateException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public class TableBuilder extends EntryBuilder {
  private final HashMap<String, List<String>> cols = new LinkedHashMap<>();
  private final List<String> foreignKeys = new LinkedList<>();
  private final List<String> primaryKeys = new LinkedList<>();
  private String tableName;

  public static TableBuilder fromClass(Class<?> clazz) throws InvalidDataTypeException, InvalidKeyException {
    es.degrassi.database.core.sql.annotations.modifier.Table table = clazz.getAnnotation(es.degrassi.database.core.sql.annotations.modifier.Table.class);
    TableBuilder builder;
    if (table == null)
      builder = new TableBuilder(clazz.getSimpleName());
    else
      builder = new TableBuilder(table.value());
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
              System.out.println("modifiers of " + field.getName() + ": " + Arrays.stream(modifiers).map(java.lang.Enum::name).toList());
              for (es.degrassi.database.core.sql.annotations.modifier.Modifier modifier : modifiers) {
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
    AtomicReference<Annotation> columnType = new AtomicReference<>(null);
    Arrays.stream(field.getAnnotations()).filter(annotation -> ColumnType.isValid(annotation.annotationType())).findFirst()
      .ifPresent(annotation -> {
        columnType.set(annotation);
        if (annotation instanceof Boolean a) modifiers.add(a.toString);
        else if (annotation instanceof Char a) modifiers.add(a.toString);
        else if (annotation instanceof Date a) modifiers.add(a.toString);
        else if (annotation instanceof DateTime a) modifiers.add(a.toString);
        else if (annotation instanceof Decimal a) modifiers.add(a.toString);
        else if (annotation instanceof Enum a)
          if (a.value().length > 0)
            modifiers.add(a.toString.replace("%s%", Arrays.toString(a.value()).replaceAll("[\\[\\]]", "")));
          else
            modifiers.add(a.toString.replace("%s%", Arrays
              .stream((java.lang.Enum<?>[]) field.getType().getEnumConstants())
              .map(java.lang.Enum::name)
              .map(name -> "\"" + name + "\"")
              .toList()
              .toString()
              .replaceAll("[\\[\\]]", "")));
        else if (annotation instanceof Float a) modifiers.add(a.toString);
        else if (annotation instanceof Int a) modifiers.add(a.toString);
        else if (annotation instanceof LongDecimal a) modifiers.add(a.toString);
        else if (annotation instanceof LongInt a) modifiers.add(a.toString);
        else if (annotation instanceof MediumDecimal a) modifiers.add(a.toString);
        else if (annotation instanceof MediumInt a) modifiers.add(a.toString);
        else if (annotation instanceof Set a) modifiers.add(a.toString.replace("%s%", Arrays.toString(a.value())).replaceAll("[\\[\\]]", ""));
        else if (annotation instanceof Timestamp a) modifiers.add(a.toString);
        else if (annotation instanceof TinyDecimal a) modifiers.add(a.toString);
        else if (annotation instanceof TinyInt a) modifiers.add(a.toString);
        else if (annotation instanceof Varchar a) modifiers.add(a.toString.replace("%s%", a.value() + ""));
      });
    if (columnType.get() == null || modifiers.isEmpty()) return this;
    for (Annotation annotation : field.getAnnotations()) {
      if (annotation instanceof AutoIncrement) {
        if (Arrays.stream(AutoIncrement.dataTypes).noneMatch(type -> DataType.fromClass(field.getType()).equals(type)))
          throw new InvalidDataTypeException(field.getType(), Number.class);
        modifiers.addAll(modifiers(AutoIncrement.keyTypes));
      } else if (annotation instanceof PrimaryKey) {
        primaryKeys.add(field.getAnnotation(Column.class).value());
//        if (modifiers.stream().anyMatch(value -> value.contains(KeyType.PRIMARY_KEY.getName())))
//          throw new InvalidKeyException(false, KeyType.PRIMARY_KEY);
//        modifiers.addAll(modifiers(PrimaryKey.keyTypes));
      } else if (annotation instanceof NotNull) {
        modifiers.addAll(modifiers(NotNull.keyTypes));
      } else if (annotation instanceof Unique) {
        modifiers.addAll(modifiers(Unique.keyTypes));
      } else if (annotation instanceof ForeingKey fk) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(KeyType.FOREING_KEY.getName());
        joiner.add("(" + field.getAnnotation(Column.class).value() + ")");
        joiner.add(KeyType.REFERENCES.getName());
        joiner.add(fk.table() + "(" + fk.columnName() + ")");
        joiner.add("ON DELETE CASCADE");
        joiner.add("ON UPDATE CASCADE");
        foreignKeys.add(joiner.toString());
      } else if (annotation instanceof Default df) {
        modifiers.addAll(modifiers(Default.keyTypes));
        if (ColumnType.mustBeQuotated(columnType.get().annotationType())) {
          modifiers.add("\"" + df.value() + "\"");
        } else {
          modifiers.add(df.value());
        }
      }
    }
    cols.put(field.getAnnotation(Column.class).value(), modifiers);
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
//    if (cols.values().stream().filter(value -> value.contains(KeyType.PRIMARY_KEY.getName())).toList().size() > 1) throw new InvalidStateException("Only one column can be Primary Key");
    StringJoiner joiner = new StringJoiner(", ");
    foreignKeys.forEach(joiner::add);
    StringJoiner pkJoiner = new StringJoiner(", ");
    primaryKeys.forEach(pkJoiner::add);
    joiner.add(
      KeyType.CONSTRAINT + " " +
      "PK_" + tableName + " " +
      KeyType.PRIMARY_KEY + "(" +
      pkJoiner +
      ")"
    );
    cols.put("", List.of(joiner.toString()));

    if (cols.values().stream().noneMatch(value -> value.contains(KeyType.PRIMARY_KEY.getName()))) throw new InvalidStateException("One column must be Primary Key");
    return new Table(cols, tableName);
  }
}
