package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.task.base.Computer.valueComputer;

import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Computer;
import org.smoothbuild.util.Empty;

public class ValueExpression extends Expression {
  private final Value value;

  public ValueExpression(Value value, CodeLocation codeLocation) {
    super(value.type(), Empty.expressionList(), codeLocation);
    this.value = checkNotNull(value);
  }

  @Override
  public Computer createComputer() {
    return valueComputer(value, codeLocation());
  }
}
