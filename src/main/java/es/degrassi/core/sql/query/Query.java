package es.degrassi.core.sql.query;

import es.degrassi.Database;
import es.degrassi.core.manager.SQLManager;
import es.degrassi.util.InvalidStateException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

@SuppressWarnings("unused")
public class Query {
  protected List<String> query = new LinkedList<>();

  public InsertQuery insert() {
    query.add("INSERT INTO");
    return new InsertQuery(this);
  }

  public SelectQuery select() {
    query.add("SELECT");
    return new SelectQuery(this);
  }

  public UpdateQuery update() {
    query.add("UPDATE");
    return new UpdateQuery(this);
  }

  public DeleteQuery delete() {
    query.add("DELETE FROM");
    return new DeleteQuery(this);
  }

  public TableQuery from() {
    query.add("FROM");
    return new TableQuery(this);
  }

  public ConditionQuery where() {
    query.add("WHERE");
    return new ConditionQuery(this);
  }

  public String build() {
    StringJoiner joiner = new StringJoiner(" ");
    query.forEach(joiner::add);
    return joiner.toString();
  }

  public boolean buildAndExecute() throws InvalidStateException, SQLException {
    Database.instance.init();
    String query = build();
    if (Database.instance.getManager() instanceof SQLManager manager) {
      return manager.getConnection().prepareStatement(query).execute();
    }
    Database.instance.close();
    return false;
  }
}
