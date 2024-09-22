package es.degrassi.database.core.sql.query;

import es.degrassi.database.Database;
import es.degrassi.database.core.sql.annotations.modifier.Table;
import es.degrassi.database.util.InvalidStateException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

@SuppressWarnings("unused")
public class UpdateQuery {
  private final Query query;

  public UpdateQuery(Query original) {
    this.query = original;
  }

  public UpdateQuery table(String table) throws InvalidStateException {
    if (table == null || table.isEmpty()) throw new InvalidStateException("Table name can not be null or empty");
    query.query.add(Database.instance.getDbName() + "." + table);
    return this;
  }

  public UpdateQuery table(Class<?> table) throws InvalidStateException {
    if (table == null || table.getAnnotation(Table.class) == null) throw new InvalidStateException("Table name can not be null or empty");
    query.query.add(Database.instance.getDbName() + "." + table.getAnnotation(Table.class).value());
    return this;
  }

  public UpdateSetQuery set() {
    query.query.add("SET");
    return new UpdateSetQuery(this);
  }

  public static class UpdateSetQuery {
    private final UpdateQuery query;
    private final List<String> sets = new LinkedList<>();

    public UpdateSetQuery(UpdateQuery query) {
      this.query = query;
    }

    public UpdateSetQuery set(String column, Object value) {
      if (value instanceof String || value instanceof Enum<?>) value = "\"" + value + "\"";
      sets.add(column + " = " + value);
      return this;
    }

    public Query build() {
      StringJoiner joiner = new StringJoiner(", ");
      sets.forEach(joiner::add);
      query.query.query.add(joiner.toString());
      return query.query;
    }
  }
}
