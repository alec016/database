## Using [db](../Use.md)

---

### Select all
```Java
    Query query = db.getManager()
      .query()
      .select()
      .all()
      .from()
      .table(db.getClass()) // select from table name
      .where()
      .create() // create condition
      .firstMember("port") // column name
      .gte() // condition
      .secondMember(3306) // value to compare
      .build() // builds condition
      .and() // FirstCondition AND SecondCondition
      .firstMember("host")
      .eq()
      .secondMember("127.0.0.1")
      .build() //builds second condition
      .build(); // build query
    table.executeQuery(query);
```
### Select some columns
```Java
    Query query = db.getManager()
      .query()
      .select()
      .values(column1, column2) // all column name you want to select(can not be empty)
      .from()
      .table(db.getClass()) // select from table name
      .where()
      .create() // create condition
      .firstMember("port") // column name
      .gte() // condition
      .secondMember(3306) // value to compare
      .build() // builds condition
      .and() // FirstCondition AND SecondCondition
      .firstMember("host")
      .eq()
      .secondMember("127.0.0.1")
      .build() //builds second condition
      .build(); // build query
    table.executeQuery(query);
```