package es.degrassi.core.builder.entry;


import es.degrassi.core.sql.Table;
import es.degrassi.core.sql.DataType;
import es.degrassi.core.sql.KeyType;
import es.degrassi.util.InvalidStateException;
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
  private Field lastAdded = null;
  private String tableName;

  public static TableBuilder fromClass(Class<?> clazz) {
    TableBuilder builder = new TableBuilder(clazz.getSimpleName());
    builder.addFields(Arrays.stream(clazz.getDeclaredFields()).filter(field -> !Modifier.isStatic(field.getModifiers())).toArray(Field[]::new));
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

  @Override
  protected TableBuilder addFiled(Field field) {
    this.lastAdded = field;
    DataType dataType = DataType.fromClass(field.getType());
    if (dataType == null) {
      lastAdded = null;
      return this;
    }
    List<String> list = new LinkedList<>();
    if (dataType == DataType.ENUM) {
      try {
        String values = Arrays.stream((Enum<?>[]) field.getType().getEnumConstants()).map(Enum::name).map(name -> "\"" + name + "\"").toList()
          .toString().replaceAll("\\[", "").replaceAll("]", "");
        list.add(dataType.name() + "(" + values + ")");
      } catch (ClassCastException e) {
        System.out.println(e.getMessage());
      }
    } else if (dataType == DataType.VARCHAR) {
      list.add(dataType.name() + "(" + 100 + ")");
    } else {
      list.add(dataType.name());
    }
    if (Modifier.isFinal(field.getModifiers())) list.add(KeyType.NOTNULL.getName());
    addField(field.getName(), list);
    return this;
  }

  public TableBuilder unsigned() throws InvalidStateException {
    if (lastAdded == null) throw new InvalidStateException("Can not set unsigned value before add new column");
    if (!lastAdded.getType().isAssignableFrom(Number.class)) throw new InvalidStateException("Can not set unsigned to a non numeric field");
    return unsigned(lastAdded.getName());
  }

  public TableBuilder unsigned(String field) throws InvalidStateException{
    if (cols.get(field).isEmpty()) throw new InvalidStateException("Can not set default value to an empty column definition");
    cols.get(field).add(KeyType.UNSIGNED.getName());
    return this;
  }

  public <T> TableBuilder defaultValue(T value) throws InvalidStateException {
    if (lastAdded == null) throw new InvalidStateException("Can not set default value before add new column");
    if (value.getClass() != lastAdded.getType()) throw new InvalidStateException("Invalid types found, can assign default value " + value + " to a type of " + lastAdded.getType().getSimpleName());
    return defaultValue(lastAdded.getName(), value);
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

  public TableBuilder asPrimaryKey() throws InvalidStateException {
    if (this.lastAdded == null) throw new InvalidStateException("Can not set Primary Key before add new column");
    return asPrimaryKey(lastAdded.getName());
  }

  public TableBuilder asPrimaryKey(String col) throws InvalidStateException {
    if (!cols.containsKey(col)) throw new InvalidStateException("Can not set Primary Key to a non existing column");
    cols.get(col).add(KeyType.PRIMARY_KEY.getName());
    return this;
  }

  public TableBuilder notNull() throws InvalidStateException {
    if (this.lastAdded == null) throw new InvalidStateException("Can not set Not Null before add new column");
    return notNull(lastAdded.getName());
  }

  public TableBuilder notNull(String col) throws InvalidStateException {
    if (!cols.containsKey(col)) throw new InvalidStateException("Can not set Not Null to a non existing column");
    cols.get(col).add(KeyType.NOT_NULL.getName());
    return this;
  }

  public TableBuilder autoincrement() throws InvalidStateException {
    if (this.lastAdded == null) throw new InvalidStateException("Can not set Autoincrement before add new column");
    autoincrement(lastAdded.getName());
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

  public TableBuilder foreingKey(String tableReference, String colReference) throws InvalidStateException {
    if (this.lastAdded == null) throw new InvalidStateException("Can not set Autoincrement before add new column");
    return foreingKey(lastAdded.getName(), tableReference, colReference);
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

  public Table build() throws InvalidStateException {
    if (tableName == null || tableName.isEmpty()) throw new InvalidStateException("Table name can not be null or empty");
    if (cols.isEmpty()) throw new InvalidStateException("Columns can not be empty");
    if (cols.values().stream().noneMatch(value -> value.contains(KeyType.PRIMARY_KEY.getName()))) throw new InvalidStateException("One column must be Primary Key");
    if (cols.values().stream().filter(value -> value.contains(KeyType.PRIMARY_KEY.getName())).toList().size() > 1) throw new InvalidStateException("Only one column can be Primary Key");
    return new Table(cols, tableName);
  }

  private boolean containsDataType(String col, DataType... types) {
    AtomicBoolean contains = new AtomicBoolean(false);
    Arrays.stream(types).map(DataType::name).forEach(type -> {
      if (cols.get(col).contains(type)) contains.set(true);
    });
    return contains.get();
  }
}
