package es.degrassi.database;

import es.degrassi.database.core.DatabaseType;
import es.degrassi.database.core.EntryType;
import es.degrassi.database.core.manager.ManagerAPI;
import es.degrassi.database.core.sql.Table;
import es.degrassi.database.util.InvalidDataTypeException;
import es.degrassi.database.util.InvalidKeyException;
import es.degrassi.database.util.InvalidStateException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public class Database {
  private static final Map<Class<?>, Table> tables = new LinkedHashMap<>();
  private static final String defaultUser = "user", defaultPassword = "pass", defaultDB = "test";
  private final String user;
  private final String pass;
  private final String host;
  private final String dbName;
  private final int port;
  private final DatabaseType type;

  private final ManagerAPI manager;

  public static Database instance;

  protected Database() {
    this(defaultUser, defaultPassword, defaultDB, DatabaseType.SQL.getDefaultHost(), DatabaseType.SQL.getDefaultPort(), DatabaseType.SQL);
  }

  protected Database(String user, String pass, String dbName, String host, int port, DatabaseType type) {
    this.type = type;
    this.user = user;
    this.dbName = dbName;
    this.pass = pass;
    this.host = host;
    this.port = port;

    manager = ManagerAPI.create(this);
    instance = this;
  }

  protected Database(String user, String pass, int port, DatabaseType type) {
    this(user, pass, defaultDB, type.getDefaultHost(), port, type);
  }

  protected Database(String user, String pass, String dbName, int port, DatabaseType type) {
    this(user, pass, dbName, type.getDefaultHost(), port, type);
  }

  protected Database(String user, String pass, String host, DatabaseType type) {
    this(user, pass, defaultDB, host, type.getDefaultPort(), type);
  }

  protected Database(String user, String pass, String dbName, String host, DatabaseType type) {
    this(user, pass, dbName, host, type.getDefaultPort(), type);
  }

  protected Database(String user, String pass, DatabaseType type) {
    this(user, pass, defaultDB, type.getDefaultHost(), type.getDefaultPort(), type);
  }

  public static Database SQLDatabase(String user, String pass, String host, int port) {
    return new Database(user, pass, host, port, DatabaseType.SQL);
  }

  public static Database SQLDatabase(String user, String pass, String host, String dbName, int port) {
    return new Database(user, pass, dbName, host, port, DatabaseType.SQL);
  }

  public static Database SQLDatabase(String user, String pass) {
    return new Database(user, pass, DatabaseType.SQL);
  }

  public static Database SQLDatabase(String user, String pass, String dbname) {
    return new Database(user, pass, dbname, DatabaseType.SQL.getDefaultHost(), DatabaseType.SQL);
  }

  public static Database SQLDatabase(String user, String pass, String host, String dbName) {
    return new Database(user, pass, dbName, host, DatabaseType.SQL);
  }

  public static Database SQLDatabase(String user, String pass, int port) {
    return new Database(user, pass, port, DatabaseType.SQL);
  }

  public static Database SQLDatabase(String user, String pass, int port, String dbName) {
    return new Database(user, pass, dbName, port, DatabaseType.SQL);
  }

  public void init() throws InvalidStateException {
    if (connect()) {
      System.out.println("Connected to Database");
    }
  }

  public void close() throws InvalidStateException {
    if (disconnect()) {
      System.out.println("Disconnected from Database");
    }
  }

  private boolean connect() throws InvalidStateException {
    synchronized (manager) {
      return manager.connect();
    }
  }

  private boolean disconnect() throws InvalidStateException {
    synchronized (manager) {
      return manager.disconnect();
    }
  }

  public boolean createEntry(EntryType entryType, Class<?> entry) throws InvalidStateException, InvalidDataTypeException, InvalidKeyException {
    if (type.isCompatibleEntry(entryType)) {
      return manager.createEntry(entry);
    }
    return false;
  }

  public void addEntry(Class<?> entryType, Table entry) {
    tables.put(entryType, entry);
  }

  public Optional<Table> get(Class<?> entryType) {
    return Optional.ofNullable(tables.get(entryType));
  }
}