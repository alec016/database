## Using [db](../Use.md)

---

### Insert
```Java
    Query query = db.getManager()
      .query()
      .insert()
      .table(db.getClass())
      .columns(table.prepareColsForInsert())
      .values(table.prepareValues(db));
    table.executeQuery(query);
```