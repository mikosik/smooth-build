package org.smoothbuild.lang.expr;

import static java.util.Arrays.asList;
import static org.smoothbuild.task.base.Computer.valueComputer;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.task.base.Computer;

public class StringLiteralExpression extends Expression {
  private final String string;

  public StringLiteralExpression(String string, CodeLocation codeLocation) {
    super(Types.STRING, asList(), codeLocation);
    this.string = string;
  }

  public Computer createComputer(ValuesDb valuesDb) {
    return valueComputer(valuesDb.string(string), codeLocation());
  }
}
