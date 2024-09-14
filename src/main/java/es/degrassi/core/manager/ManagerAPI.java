package es.degrassi.core.manager;

import es.degrassi.Database;
import es.degrassi.core.builder.entry.EntryBuilder;
import es.degrassi.util.InvalidStateException;
import java.sql.SQLException;
import java.util.List;
import lombok.Getter;

@Getter
public abstract class ManagerAPI {
  protected final Database database;
  
  protected ManagerAPI(Database database) {
    this.database = database;
  }

  public static ManagerAPI create(Database database) {
    return switch (database.getType()) {
      case SQL -> new SQLManager(database);
      case MONGO -> new MongoManager(database, "default");
    };
  }

  public static ManagerAPI create(Database database, String dbName) {
    return switch (database.getType()) {
      case SQL -> new SQLManager(database);
      case MONGO -> new MongoManager(database, dbName);
    };
  }

  public abstract boolean connect() throws InvalidStateException;

  public abstract boolean disconnect() throws InvalidStateException;

  public abstract boolean createEntry(Class<?> entry) throws InvalidStateException;

  public abstract EntryBuilder entryBuilder();

  public abstract List<?> select(Class<?> type) throws InvalidStateException;
}
