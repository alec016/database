package es.degrassi.database.core.sql;

import es.degrassi.database.Database;
import es.degrassi.database.core.manager.SQLManager;
import es.degrassi.database.core.sql.annotations.modifier.AutoIncrement;
import es.degrassi.database.core.sql.annotations.modifier.IncompatibleModifiers;
import es.degrassi.database.core.sql.query.Query;
import es.degrassi.database.util.InvalidStateException;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

@SuppressWarnings("unused")
public record Table(HashMap<String, List<String>> cols, String tableName) {

  public <T> boolean insert(String dbName, T object) throws InvalidStateException, SQLException {
    Database.instance.init();
    if (Database.instance.getManager() instanceof SQLManager manager) {
      boolean success = !manager.getConnection().prepareStatement(prepareInsert(dbName, object)).execute();
      Database.instance.close();
      return success;
    }
    Database.instance.close();
    return false;
  }
  public boolean executeQuery(Query query) throws InvalidStateException, SQLException {
    Database.instance.init();
    if (Database.instance.getManager() instanceof SQLManager manager) {
      boolean success = !manager.getConnection().prepareStatement(query.build()).execute();
      Database.instance.close();
      return success;
    }
    Database.instance.close();
    return false;
  }

  public List<?> select(String dbName) throws InvalidStateException, SQLException {
    List<Object> list = new LinkedList<>();
    Database.instance.init();
    if (Database.instance.getManager() instanceof SQLManager manager) {
      ResultSet rs = manager.getConnection().prepareStatement(prepareSelectAllStatement(dbName)).executeQuery();
      while(rs.next()) {
        List<Object> values = new LinkedList<>();
        for (String col : cols.keySet()) {
          String modified = this.cols.get(col).get(0).toUpperCase(Locale.ROOT);
          if (modified.contains("(")) modified = modified.substring(0, modified.indexOf("("));
          Object object = switch(modified) {
            case "INT", "MEDIUMINT" -> rs.getInt(col);
            case "VARCHAR", "TEXT", "SET", "ENUM" -> rs.getString(col);
            case "DECIMAL", "TINYDECIMAL", "MEDIUMDECIMAL", "LONGDECIMAL" -> rs.getDouble(col);
            case "DATE" -> rs.getDate(col);
            case "TIMESTAMP" -> new Date(rs.getTimestamp(col).getTime());
            case "TINYINT", "SMALLINT" -> rs.getShort(col);
            case "LONGINT" -> rs.getLong(col);
            case "BLOB" -> rs.getBlob(col); // see what class fits better
            case "DATETIME" -> new Date(rs.getTime(col).getTime());
            case "CHAR" -> rs.getString(col).charAt(0);
            case "FLOAT" -> rs.getFloat(col);
            case "BOOLEAN" -> rs.getBoolean(col);
//              case "GEOMETRY" -> rs.get; // see what class fits better
//              case "YEAR" -> rs.get; // see what class fits better
            default -> throw new IllegalStateException("Unexpected value: " + modified);
          };
          values.add(object);
        }
        list.add(values);
      }
    }
    Database.instance.close();
    return list;
  }

  public List<Object> selectWithQuery(Query query) throws InvalidStateException, SQLException {
    List<Object> list = new LinkedList<>();
    Database.instance.init();
    if (Database.instance.getManager() instanceof SQLManager manager) {
      ResultSet rs = manager.getConnection().prepareStatement(query.build()).executeQuery();
      while(rs.next()) {
        List<Object> values = new LinkedList<>();
        for (String col : cols.keySet()) {
          String modified = this.cols.get(col).get(0).toUpperCase(Locale.ROOT);
          if (modified.contains("(")) modified = modified.substring(0, modified.indexOf("("));
          Object object = switch(modified) {
            case "INT", "MEDIUMINT" -> rs.getInt(col);
            case "VARCHAR", "TEXT", "SET", "ENUM" -> rs.getString(col);
            case "DECIMAL", "TINYDECIMAL", "MEDIUMDECIMAL", "LONGDECIMAL" -> rs.getDouble(col);
            case "DATE" -> rs.getDate(col);
            case "TIMESTAMP" -> new Date(rs.getTimestamp(col).getTime());
            case "TINYINT", "SMALLINT" -> rs.getShort(col);
            case "LONGINT" -> rs.getLong(col);
            case "BLOB" -> rs.getBlob(col); // see what class fits better
            case "DATETIME" -> new Date(rs.getTime(col).getTime());
            case "CHAR" -> rs.getString(col).charAt(0);
            case "FLOAT" -> rs.getFloat(col);
            case "BOOLEAN" -> rs.getBoolean(col);
//              case "GEOMETRY" -> rs.get; // see what class fits better
//              case "YEAR" -> rs.get; // see what class fits better
            default -> throw new IllegalStateException("Unexpected value: " + modified);
          };
          values.add(object);
        }
        list.add(values);
      }
    }
    Database.instance.close();
    return list;
  }

  public List<Object> select(String dbname, String... cols) throws InvalidStateException, SQLException {
    List<Object> list = new LinkedList<>();
    if (!Arrays.stream(cols).allMatch(this.cols::containsKey)) throw new InvalidStateException("can not select non existing cols for this table");
    Database.instance.init();
    if (Database.instance.getManager() instanceof SQLManager manager) {
      ResultSet rs = manager.getConnection().prepareStatement(prepareSelectStatement(dbname, cols)).executeQuery();
      while(rs.next()) {
        for (String col : cols) {
          List<Object> values = new LinkedList<>();
          Object object = switch(this.cols.get(col).get(0).toUpperCase(Locale.ROOT)) {
            case "INT", "MEDIUMINT" -> rs.getInt(col);
            case "VARCHAR", "TEXT", "SET", "ENUM" -> rs.getString(col);
            case "DECIMAL", "TINYDECIMAL", "MEDIUMDECIMAL", "LONGDECIMAL" -> rs.getDouble(col);
            case "DATE" -> rs.getDate(col);
            case "TIMESTAMP" -> new Date(rs.getTimestamp(col).getTime());
            case "TINYINT", "SMALLINT" -> rs.getShort(col);
            case "LONGINT" -> rs.getLong(col);
            case "BLOB" -> rs.getBlob(col); // see what class fits better
            case "DATETIME" -> new Date(rs.getTime(col).getTime());
            case "CHAR" -> rs.getString(col).charAt(0);
            case "FLOAT" -> rs.getFloat(col);
            case "BOOLEAN" -> rs.getBoolean(col);
//              case "GEOMETRY" -> rs.get; // see what class fits better
//              case "YEAR" -> rs.get; // see what class fits better
            default -> throw new IllegalStateException("Unexpected value: " + this.cols.get(col).get(0).toUpperCase(Locale.ROOT));
          };
          values.add(object);
          list.add(values);
        }
      }
    }
    Database.instance.close();
    return list;
  }

  public String prepareSelectStatement(String dbName, String... cols) {
    if (cols.length == this.cols.size() || cols.length == 0) return prepareSelectAllStatement(dbName);
    StringJoiner joiner = new StringJoiner(", ");
    Arrays.stream(cols).forEach(joiner::add);
    return "SELECT " + joiner + " FROM " + dbName + "." + tableName;
  }

  private String prepareSelectAllStatement(String dbName) {
    return "SELECT * FROM " + dbName + "." + tableName;
  }

  private String prepareCreationStatement(String dbName) {
    return "CREATE TABLE " + dbName + "." + tableName + "(" + prepareCols() + ");";
  }

  public String prepareCreationIfNotExistsStatement(String dbName) {
    return "CREATE TABLE IF NOT EXISTS " + dbName + "." + tableName + "(" + prepareCols() + ");";
  }

  public <T> String prepareInsert(String dbName, T object) {
    return "INSERT INTO " + dbName + "." + tableName + " (" + prepareColsForInsert() + ") VALUES (" + prepareValues(object) + ")";
  }

  public <T> String prepareValues(T object) {
    StringJoiner joiner = new StringJoiner(", ");
    Arrays
      .stream(object.getClass().getDeclaredFields())
      .filter(field -> Arrays
        .stream(field.getAnnotations())
        .noneMatch(annotation -> annotation instanceof AutoIncrement)
      ).filter(field -> Arrays
        .stream(field.getAnnotations())
        .anyMatch(annotation -> Arrays
          .stream(annotation.getClass().getAnnotations())
          .filter(ann -> ann instanceof IncompatibleModifiers)
          .map(ann -> (IncompatibleModifiers) ann)
          .map(IncompatibleModifiers::modifier)
          .allMatch(modifiers -> {
            boolean accepts = true;
            System.out.println("modifiers of " + field.getName() + ": " + Arrays.stream(modifiers).map(Enum::name).toList());
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
      ).forEach(field -> {
        if (!cols.containsKey(field.getName())) return;
        try {
          field.setAccessible(true);
          if (field.getType().isAssignableFrom(String.class) || field.getType().isEnum()) {
            joiner.add("\"" + field.get(object) + "\"");
          } else {
            joiner.add(field.get(object) + "");
          }
        } catch (IllegalAccessException | InaccessibleObjectException | SecurityException e) {
          System.out.println(e.getMessage());
          joiner.add("");
        }
      });
    return joiner.toString();
  }

  public String prepareColsForInsert() {
    StringBuilder builder = new StringBuilder();
    StringJoiner joiner = new StringJoiner(", ");
    cols.forEach((key, value) -> {
      if (value.contains(KeyType.AUTOINCREMENT.getName()))
        return;
      joiner.add(key);
    });
    builder.append(joiner);
    return builder.toString();
  }

  public String prepareCols() {
    StringBuilder builder = new StringBuilder();
    StringJoiner j = new StringJoiner(", ");
    cols.entrySet().stream().map(entry -> {
      String name = entry.getKey();
      List<String> type = entry.getValue();
      StringJoiner joiner = new StringJoiner(" ");
      type.forEach(joiner::add);
      return name.isEmpty() ? joiner.toString() : name + " " + joiner;
    }).forEach(j::add);
    builder.append(j);
    return builder.toString();
  }

  public void createIfNotExists(String dbName) throws InvalidStateException, SQLException {
    Database.instance.init();
    if (Database.instance.getManager() instanceof SQLManager manager) {
      manager.getConnection().prepareStatement(prepareCreationIfNotExistsStatement(dbName))
        .execute();
    }
    Database.instance.close();
  }

  public void create(String dbName) throws InvalidStateException, SQLException {
    Database.instance.init();
    if (Database.instance.getManager() instanceof SQLManager manager) {
      manager.getConnection().prepareStatement(prepareCreationStatement(dbName))
        .execute();
    }
    Database.instance.close();
  }
}
