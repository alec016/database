## Using [db](../Use.md)

---

### Delete with condition
```Java
  // where statement is required to delete something
    Query query = db.getManager()
      .query()
      .delete()
      .table(db.getClass())
      .where()
      .create()
      .firstMember("id")
      .eq()
      .secondMember(2)
      .build()
      .build();
    table.executeQuery(query);
```