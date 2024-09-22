package es.degrassi.database.core.manager;

import es.degrassi.database.Database;
import es.degrassi.database.core.builder.entry.TableBuilder;
import es.degrassi.database.core.sql.Table;
import es.degrassi.database.util.InvalidDataTypeException;
import es.degrassi.database.util.InvalidKeyException;
import es.degrassi.database.util.InvalidStateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;

@SuppressWarnings("unused")
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
  public boolean createEntry(Class<?> entry) throws InvalidStateException, InvalidDataTypeException, InvalidKeyException {
    Table table = TableBuilder.fromClass(entry).build();
    database.addEntry(entry, table);

    try {
      System.out.println(table.prepareCreationIfNotExistsStatement(database.getDbName()));
      table.createIfNotExists(database.getDbName());
      System.out.println("Table " + table.tableName() + " created successfully");
    } catch (SQLException | InvalidStateException exception) {
      System.out.println("Can not create table, exists or invalid statement");
    }

    return database.get(entry).isPresent();
  }

  @Override
  public List<?> select(Class<?> type) {
    AtomicReference<List<?>> list = new AtomicReference<>(new LinkedList<>());
    database.get(type).ifPresent(table -> {
      try {
        list.set(table.select(database.getDbName()));
      } catch (SQLException | InvalidStateException exception) {
        System.out.println(exception.getMessage());
        System.out.println("Cant select data");
      }
    });
    return list.get();
  }
}
