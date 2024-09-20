package es.degrassi.core.sql.query;

import es.degrassi.Database;
import es.degrassi.util.InvalidStateException;

@SuppressWarnings("unused")
public class TableQuery {
  private final Query query;

  public TableQuery(Query original) {
    this.query = original;
  }

  public Query table(String table) throws InvalidStateException {
    if (table == null || table.isEmpty()) throw new InvalidStateException("Table name can not be null or empty");
    query.query.add(Database.instance.getDbName() + "." + table);
    return query;
  }

  public Query table(Class<?> table) throws InvalidStateException {
    if (table == null || table.getAnnotation(es.degrassi.core.sql.annotations.modifier.Table.class) == null) throw new InvalidStateException("Table name can not be null or empty");
    query.query.add(Database.instance.getDbName() + "." + table.getAnnotation(es.degrassi.core.sql.annotations.modifier.Table.class));
    return query;
  }
}
