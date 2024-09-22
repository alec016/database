package es.degrassi.database.core.sql.query;

import es.degrassi.database.util.InvalidStateException;
import java.util.Arrays;
import java.util.StringJoiner;

@SuppressWarnings("unused")
public class SelectQuery {
  private final Query query;

  public SelectQuery(Query original) {
    this.query = original;
  }

  public Query all() {
    this.query.query.add("*");
    return query;
  }

  public Query values(String...columns) throws InvalidStateException {
    if (columns.length == 0) throw new InvalidStateException("Column selection can not be empty");
    StringJoiner joiner = new StringJoiner(", ");
    Arrays.asList(columns).forEach(joiner::add);
    query.query.add(joiner.toString());
    return query;
  }
}
