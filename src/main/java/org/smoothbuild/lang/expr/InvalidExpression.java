package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Computer;

public class InvalidExpression extends Expression {
  private final Type type;

  public InvalidExpression(Type type, CodeLocation codeLocation) {
    super(type, asList(), codeLocation);
    this.type = checkNotNull(type);
  }

  public Type type() {
    return type;
  }

  public Computer createComputer(ValuesDb valuesDb) {
    throw new RuntimeException("Cannot create Computer for invalid expression.");
  }
}
