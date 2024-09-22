package es.degrassi.database.core.manager;

import es.degrassi.database.Database;
import es.degrassi.database.core.builder.entry.EntryBuilder;
import es.degrassi.database.core.sql.query.Query;
import es.degrassi.database.util.InvalidDataTypeException;
import es.degrassi.database.util.InvalidKeyException;
import es.degrassi.database.util.InvalidStateException;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@SuppressWarnings("unused")
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

  public void create(Creation... creationCallbacks) {
    Arrays.stream(creationCallbacks).forEach(callback -> {
      try {
        createEntry(callback.get());
      } catch (InvalidStateException | InvalidDataTypeException | InvalidKeyException e) {
        System.out.println(e.getMessage());
      }
    });
  }

  public Query query() {
    return new Query();
  }

  public abstract boolean connect() throws InvalidStateException;

  public abstract boolean disconnect() throws InvalidStateException;

  public abstract boolean createEntry(Class<?> entry) throws InvalidStateException, InvalidDataTypeException, InvalidKeyException;

  public abstract EntryBuilder entryBuilder();

  public abstract List<?> select(Class<?> type) throws InvalidStateException;
}
