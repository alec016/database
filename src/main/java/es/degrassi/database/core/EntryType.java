package es.degrassi.database.core;

@SuppressWarnings("unused")
public enum EntryType {
  TABLE, COLLECTION;

  public boolean isForSQL() {
    return this == TABLE;
  }

  public boolean isForMongo() {
    return this == COLLECTION;
  }
}
