package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Computer;

public class InvalidExpression extends Expression {
  private final Type type;

  public InvalidExpression(Type type, CodeLocation codeLocation) {
    super(type, asList(), codeLocation);
    this.type = checkNotNull(type);
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public Computer createComputer() {
    throw new RuntimeException("Cannot create Computer for invalid expression.");
  }
}
