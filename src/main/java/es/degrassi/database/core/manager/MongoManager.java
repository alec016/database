package es.degrassi.database.core.manager;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import es.degrassi.database.Database;
import es.degrassi.database.core.builder.entry.CollectionBuilder;
import es.degrassi.database.util.InvalidStateException;
import java.util.List;

@SuppressWarnings("unused")
public class MongoManager extends ManagerAPI {
  private static final Gson GSON = new GsonBuilder()
    .serializeNulls()
    .setFormattingStyle(FormattingStyle.PRETTY)
    .setPrettyPrinting()
    .create();

  private MongoClient client = null;
  private MongoDatabase connection = null;
  private final String dbName;

  public MongoManager(Database database, String dbName) {
    super(database);
    this.dbName = dbName;
  }

  public boolean connect() throws InvalidStateException {
    String uri = "mongodb://" + database.getUser() + ":" + database.getPass() + "@" + database.getHost() + ":" + database.getPort();
    try {
      client = MongoClients.create(uri);
      connection = client.getDatabase(dbName);
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new InvalidStateException("Couldn't use or create database: " + dbName);
    }
    return client != null;
  }

  public boolean disconnect() {
    if (client != null) {
      client.close();
      client = null;
      connection = null;
      return true;
    }
    if (connection != null) {
      connection = null;
      return true;
    }
    return false;
  }

  @Override
  public boolean createEntry(Class<?> entry) throws InvalidStateException {
    if (!connection.listCollectionNames().map(collectionName -> collectionName).into(List.of()).contains(entry.getSimpleName()))
      connection.createCollection(entry.getSimpleName());
    return connection.listCollectionNames().map(collectionName -> collectionName).into(List.of()).contains(entry.getSimpleName());
  }

  @Override
  public CollectionBuilder entryBuilder() {
    return new CollectionBuilder();
  }

  @Override
  public List<?> select(Class<?> type) throws InvalidStateException {
    return List.of();
  }
}
