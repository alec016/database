# Connect to database

---

```Java
import es.degrassi.Database;

class YourClass {
  public void yourMethod() {
    /*
     * Default host -> "127.0.0.1"
     * Default port -> 3306
     * Default database -> ""
     */
    // String user, String password, String host, int port
    Database db = Database.SQLDatabase(user, password, host, port);
    // String user, String password, String host, String database, int port
    Database db = Database.SQLDatabase(user, password, host, database, port);
    // String user, String password
    Database db = Database.SQLDatabase(user, password);
    // String user, String password, String database
    Database db = Database.SQLDatabase(user, password, database);
    // String user, String password, String host, String database
    Database db = Database.SQLDatabase(user, password, host, database);
    // String user, String password, int port
    Database db = Database.SQLDatabase(user, password, port);
    // String user, String password, int port, String database
    Database db = Database.SQLDatabase(user, password, port, database);
  }
}
```

## [Create/get table](./useguide/CreateTable.md)
## [Select data](./useguide/Select.md)
## [Insert data](./useguide/Insert.md)
## [Update data](./useguide/Update.md)
## [Delete data](./useguide/Delete.md)