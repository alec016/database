package es.degrassi.core.sql.query;

import es.degrassi.util.InvalidStateException;

@SuppressWarnings("unused")
public class ConditionQuery {
  private final Query query;
  private final StringBuilder queryString = new StringBuilder();

  public ConditionQuery(Query original) {
    this.query = original;
  }

  public Condition create() {
    return new Condition(this);
  }

  public Condition and() {
    queryString.append(" ").append("AND").append(" ");
    return new Condition(this);
  }

  public Condition or() {
    queryString.append(" ").append("OR").append(" ");
    return new Condition(this);
  }

  public Query build() {
    query.query.add(queryString.toString());
    return query;
  }

  public static class Condition {
    private String condition;
    private Object firstMember;
    private Object secondMember;
    private final ConditionQuery query;
    public Condition(ConditionQuery query) {
      this.query = query;
    }

    public Condition firstMember(Object first) {
      this.firstMember = first;
      return this;
    }

    public Condition secondMember(Object second) {
      this.secondMember = second;
      return this;
    }

    public ConditionQuery build() throws InvalidStateException {
      if (firstMember == null || secondMember == null) throw new InvalidStateException("Either first or second member can not be null");
      if (condition == null) throw new InvalidStateException("Condition can not be null");
      if (secondMember instanceof String) secondMember = "\"" + secondMember + "\"";
      query.queryString.append(firstMember).append(" ").append(condition).append(" ").append(secondMember);
      return query;
    }

    /**
     * Greater than
     */
    public Condition gt() {
      condition = Conditions.GREATER.representation;
      return this;
    }

    /**
     * Greater or equals than
     */
    public Condition gte() {
      condition = Conditions.GREATER_OR_EQUAL.representation;
      return this;
    }

    /**
     * Less than
     */
    public Condition lt() {
      condition = Conditions.LESS.representation;
      return this;
    }

    /**
     * Less or equals than
     */
    public Condition lte() {
      condition = Conditions.LESS_OR_EQUAL.representation;
      return this;
    }

    /**
     * Equals
     */
    public Condition eq() {
      condition = Conditions.EQUAL.representation;
      return this;
    }
  }

  private enum Conditions {
    GREATER(">"),
    GREATER_OR_EQUAL(">="),
    LESS("<"),
    LESS_OR_EQUAL("<="),
    EQUAL("=");

    private final String representation;
    Conditions(String representation) {
      this.representation = representation;
    }
  }
}
