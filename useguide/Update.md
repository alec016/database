## Using [db](../Use.md)

---

### Update With Condition
```Java
  // Where statement is required to execute the query
    Query query = db.getManager()
      .query()
      .update()
      .table(db.getClass())
      .set()
      .set("host", "localhost")
      .set("port", 3307)
      .build()
      .where()
      .create()
      .firstMember("id")
      .eq()
      .secondMember(2)
      .build()
      .build();
    table.executeQuery(query);
```