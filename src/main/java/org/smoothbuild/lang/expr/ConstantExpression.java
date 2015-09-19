package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.task.compute.Computer.constantComputer;

import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.compute.Computer;
import org.smoothbuild.util.Empty;

public class ConstantExpression extends Expression {
  private final Value value;

  public ConstantExpression(Value value, CodeLocation codeLocation) {
    super(value.type(), Empty.expressionList(), codeLocation);
    this.value = checkNotNull(value);
  }

  @Override
  public Computer createComputer() {
    return constantComputer(type(), value, codeLocation());
  }
}
