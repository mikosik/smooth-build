package org.smoothbuild.lang.expr;

import static java.util.Arrays.asList;
import static org.smoothbuild.task.base.Evaluator.valueEvaluator;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.function.base.Scope;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.task.base.Evaluator;

public class StringLiteralExpression extends Expression {
  private final String string;

  public StringLiteralExpression(String string, Location location) {
    super(Types.STRING, asList(), location);
    this.string = string;
  }

  @Override
  public Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope) {
    return valueEvaluator(valuesDb.string(string), location());
  }
}
