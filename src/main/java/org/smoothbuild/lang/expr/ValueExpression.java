package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static org.smoothbuild.task.base.Computer.valueComputer;

import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Computer;

public class ValueExpression extends Expression {
  private final Value value;

  public ValueExpression(Value value, CodeLocation codeLocation) {
    super(value.type(), asList(), codeLocation);
    this.value = checkNotNull(value);
  }

  @Override
  public Computer createComputer() {
    return valueComputer(value, codeLocation());
  }
}
