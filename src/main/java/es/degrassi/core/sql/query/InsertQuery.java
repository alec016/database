package es.degrassi.core.sql.query;

import es.degrassi.Database;
import es.degrassi.core.sql.annotations.modifier.Table;
import es.degrassi.util.InvalidStateException;
import java.util.Arrays;
import java.util.StringJoiner;

@SuppressWarnings("unused")
public class InsertQuery {
  private final Query query;
  private int columnCount = 0;

  public InsertQuery(Query original) {
    this.query = original;
  }

  public InsertQuery table(String table) throws InvalidStateException {
    if (table == null || table.isEmpty()) throw new InvalidStateException("Table name can not be null or empty");
    query.query.add(Database.instance.getDbName() + "." + table);
    return this;
  }

  public InsertQuery table(Class<?> table) throws InvalidStateException {
    if (table == null || table.getAnnotation(Table.class) == null) throw new InvalidStateException("Table name can not be null or empty");
    query.query.add(Database.instance.getDbName() + "." + table.getAnnotation(Table.class).value());
    return this;
  }

  public InsertQuery columns(String...columns) throws InvalidStateException {
    if (columns.length == 0) throw new InvalidStateException("Columns can not be empty");
    StringJoiner joiner = new StringJoiner(", ");
    Arrays.stream(columns).forEach(joiner::add);
    query.query.add("(" + joiner + ")");
    columnCount = columns.length;
    return this;
  }

  public Query values(Object...objects) throws InvalidStateException {
    if (objects.length != columnCount)
      throw new InvalidStateException("Values to insert must be equals than column definition. Expected: " + columnCount + " but found: " + objects.length);
    StringJoiner joiner = new StringJoiner(", ");
    Arrays.stream(objects).map(Object::toString).forEach(joiner::add);
    query.query.add("VALUES");
    query.query.add("(" + joiner + ")");
    return query;
  }
}
