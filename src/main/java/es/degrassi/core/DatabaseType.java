package es.degrassi.core;

import lombok.Getter;

@SuppressWarnings("unused")
@Getter
public enum DatabaseType {
  SQL(3306), MONGO(27017);

  private final int defaultPort;
  private final String defaultHost = "127.0.0.1";

  DatabaseType(int port) {
    this.defaultPort = port;
  }

  public boolean isCompatibleEntry(EntryType entryType) {
    return (this == SQL && entryType.isForSQL()) || (this == MONGO && entryType.isForMongo());
  }
}
