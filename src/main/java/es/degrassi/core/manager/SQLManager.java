package es.degrassi.core.manager;

import es.degrassi.Database;
import es.degrassi.core.builder.entry.TableBuilder;
import es.degrassi.core.sql.DataType;
import es.degrassi.core.sql.KeyType;
import es.degrassi.core.sql.Table;
import es.degrassi.util.InvalidStateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import lombok.Getter;

@Getter
public class SQLManager extends ManagerAPI {
  private Connection connection = null;

  public SQLManager(Database database) {
    super(database);
  }

  public boolean connect() throws InvalidStateException {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      String url = "jdbc:mysql://" + database.getHost() + ":" + database.getPort() + "/?serverTimezone=UTC";
      this.connection = DriverManager.getConnection(url, database.getUser(), database.getPass());
    } catch (ClassNotFoundException | SQLException e) {
      if (e instanceof ClassNotFoundException) {
        throw new InvalidStateException("Unexpected error occurs, driver is incorrect");
      } else {
        System.out.println(e.getMessage());
        throw new InvalidStateException("Unexpected error occurs, either user or password are incorrect");
      }
    }
    return this.connection != null;
  }

  public boolean disconnect() {
    if (this.connection != null) {
      try {
        if (!this.connection.isClosed())
          this.connection.close();
        this.connection = null;
      } catch (SQLException e) {
        System.out.println("Couldn't close connection");
      }
    }
    return this.connection == null;
  }

  @Override
  public TableBuilder entryBuilder() {
    return TableBuilder.empty();
  }

  /**
   *
   * @param entry object to try to convert into a table
   * @return true if the table can be created(and creates it)
   * @throws InvalidStateException if the object is null or hasn't any fields
   */
  @Override
  public boolean createEntry(Class<?> entry) throws InvalidStateException {
    Table table = TableBuilder.fromClass(entry)
//      .asPrimaryKey("user")
      .addField("id", DataType.INT, KeyType.AUTOINCREMENT, KeyType.PRIMARY_KEY)
      .defaultValue("port", 3306)
      .defaultValue("host", "127.0.0.1")
      .build();
    System.out.println("======================================================================================================");
    System.out.println("Creating table...");
    try {
      System.out.println(table.prepareCreationIfNotExistsStatement(database.getDbName()));
      table.createIfNotExists(getDatabase().getDbName());
      System.out.println("Table " + table.tableName() + " created successfully");
    } catch (SQLException exception) {
      System.out.println("Can not create table, exists or invalid statement");
    }
    System.out.println("======================================================================================================");
    System.out.println("Selecting data...");
    try {
      System.out.println(table.prepareSelectStatement(database.getDbName()));
      System.out.println(table.select(database.getDbName()));
    } catch(SQLException exception) {
      System.out.println(exception.getMessage());
      System.out.println("Can not retrieve data");
    }
    System.out.println("======================================================================================================");
    System.out.println("Inserting data...");
    System.out.println(table.prepareInsert(database.getDbName(), database));
    try {
      System.out.println(table.insert(database.getDbName(), database));
    } catch(InvalidStateException | SQLException exception) {
      System.out.println(exception.getMessage());
      System.out.println("Can not insert data");
    }
    System.out.println("======================================================================================================");
    System.out.println("Deleting data...");
    System.out.println("======================================================================================================");
    return false;
  }

  @Override
  public List<?> select(Class<?> type) throws InvalidStateException {
    Table table = TableBuilder.fromClass(type)
//      .asPrimaryKey("user")
      .addField("id", DataType.INT, KeyType.AUTOINCREMENT, KeyType.PRIMARY_KEY)
      .defaultValue("port", 3306)
      .defaultValue("host", "127.0.0.1")
      .build();
    try {
      return table.select(database.getDbName());
    } catch (SQLException exception) {
      System.out.println(exception.getMessage());
      System.out.println("Cant select data");
    }
    return List.of();
  }
}
