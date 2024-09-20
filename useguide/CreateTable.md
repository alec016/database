## Using [db](../Use.md)

---

## Create table in a class
```Java
@Table("tableName")
public class YourTableClass {
  @AutoIncrement
  @PrimaryKey
  @Column("id")
  @Int
  private final int id;

  @Default("Your default value")
  @Column("column1")
  @Varchar
  private String column1;
  
  @NotNull
  @Column("column2")
  @Text
  private final String column2;

  @NotNull
  @Column("type")
  @Enum // -> ENUM("DATABASE_TYPE1", "DATABASE_TYPE2")
  private final DatabaseType type;
  
  @NotNull
  @Column("type2")
  //@Enum("DATABASE_TYPE1", "DATABASE_TYPE2", "DATABASE_TYPE3") // -> ENUM("DATABASE_TYPE1", "DATABASE_TYPE2", "DATABASE_TYPE3")
  private String type2;
  
  @Column("tableName") // -> compile error, column fields can not be static
  private static String tableName = ""; // no valid
    
  enum DatabaseType {
    DATABASE_TYPE1, DATABASE_TYPE2
  }
}

```

### Create tables
```Java
    db.getManager().create(
      () -> YoutClassTable.class
    );
```
### Get table
```Java
    db.get(Database.class); // Optional<Table>
```