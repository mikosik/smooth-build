package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Computer;
import org.smoothbuild.util.Empty;

public class InvalidExpression extends Expression {
  private final Type type;

  public InvalidExpression(Type type, CodeLocation codeLocation) {
    super(type, Empty.expressionList(), codeLocation);
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
