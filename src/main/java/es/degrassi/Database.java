package es.degrassi;

import es.degrassi.core.DatabaseType;
import es.degrassi.core.EntryType;
import es.degrassi.core.manager.ManagerAPI;
import es.degrassi.core.sql.Table;
import es.degrassi.core.sql.annotations.AutoIncrement;
import es.degrassi.core.sql.annotations.Default;
import es.degrassi.core.sql.annotations.NotNull;
import es.degrassi.core.sql.annotations.PrimaryKey;
import es.degrassi.util.InvalidDataTypeException;
import es.degrassi.util.InvalidKeyException;
import es.degrassi.util.InvalidStateException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

@Getter
public class Database {
  private static final Map<Class<?>, Table> tables = new LinkedHashMap<>();
  private static final String defaultUser = "user", defaultPassword = "pass", defaultDB = "test";
  @NotNull
  @Default(defaultUser)
  private final String user;
  @NotNull
  @Default(defaultPassword)
  private final String pass;
  @NotNull
  @Default("127.0.0.1")
  private final String host;
  @NotNull
  @Default(defaultDB)
  private final String dbName;
  @NotNull
  @Default("3306")
  private final int port;
  @NotNull
  private final DatabaseType type;
  @PrimaryKey
  @AutoIncrement
  private final int id = 0;

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

  public static void main(String[] args) {
    Database db = SQLDatabase("libuser", "libUser123", "s6_bungee");
//        .getManager()
//        .createEntry(Database.class);
    System.out.println("======================================================================================================");
    System.out.println("Creating tables...");
    db.getManager().create(
      () -> Database.class
//      () -> TableBuilder.class
    );

    db.get(Database.class).ifPresent(table -> {

      System.out.println("======================================================================================================");
      System.out.println("Selecting data...");
      try {
        System.out.println(table.prepareSelectStatement(db.getDbName()));
        System.out.println(table.select(db.getDbName()));
      } catch(SQLException | InvalidStateException exception) {
        System.out.println(exception.getMessage());
        System.out.println("Can not retrieve data");
      }
      System.out.println("======================================================================================================");
      System.out.println("Inserting data...");
      System.out.println(table.prepareInsert(db.getDbName(), db));
      try {
        System.out.println(table.insert(db.getDbName(), db));
      } catch(InvalidStateException | SQLException exception) {
        System.out.println(exception.getMessage());
        System.out.println("Can not insert data");
      }
      System.out.println("======================================================================================================");
      System.out.println("Deleting data...");
      System.out.println("======================================================================================================");
      System.out.println();
    });

  }

  @Override
  public String toString() {
    return "Database{" +
      "user='" + user + '\'' +
      ", pass='" + pass + '\'' +
      ", host='" + host + '\'' +
      ", dbName='" + dbName + '\'' +
      ", port=" + port +
      ", type=" + type +
      '}';
  }
}