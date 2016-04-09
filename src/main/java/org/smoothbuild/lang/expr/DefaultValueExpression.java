package org.smoothbuild.lang.expr;

import static java.util.Arrays.asList;
import static org.smoothbuild.task.base.Computer.valueComputer;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Computer;

public class DefaultValueExpression extends Expression {
  public DefaultValueExpression(Type type, CodeLocation codeLocation) {
    super(type, asList(), codeLocation);
  }

  public Computer createComputer(ValuesDb valuesDb) {
    return valueComputer(type().defaultValue(valuesDb), codeLocation());
  }
}
