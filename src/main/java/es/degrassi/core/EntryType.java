package es.degrassi.core;

public enum EntryType {
  TABLE, COLLECTION;

  public boolean isForSQL() {
    return this == TABLE;
  }

  public boolean isForMongo() {
    return this == COLLECTION;
  }
}
