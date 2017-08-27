package org.smoothbuild.lang.expr;

import static java.util.Arrays.asList;
import static org.smoothbuild.task.base.Evaluator.valueEvaluator;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Evaluator;

public class DefaultValueExpression extends Expression {
  public DefaultValueExpression(Type type, Location location) {
    super(type, asList(), location);
  }

  public Evaluator createEvaluator(ValuesDb valuesDb) {
    return valueEvaluator(type().defaultValue(valuesDb), location());
  }
}
