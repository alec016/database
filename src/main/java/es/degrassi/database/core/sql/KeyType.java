package es.degrassi.database.core.sql;

import lombok.Getter;

@SuppressWarnings("unused")
@Getter
public enum KeyType {
  PRIMARY_KEY("PRIMARY KEY"),
  NOT_NULL("NOTNULL"),
  FOREING_KEY("FOREIGN KEY"),
  REFERENCES("REFERENCES"),
  NOTNULL("NOT NULL"),
  DEFAULT("DEFAULT"),
  UNSIGNED("UNSIGNED"),
  CONSTRAINT("CONSTRAINT"),
  UNIQUE("UNIQUE"),
  AUTOINCREMENT("AUTO_INCREMENT"),
  AUTOGENERATED("AUTO_GENERATED");

  private final String name;

  KeyType(String value) {
    this.name = value;
  }

}