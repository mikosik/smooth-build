package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.task.base.Evaluator.valueEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class LiteralExpression extends Expression {
  private final Value value;

  public LiteralExpression(Value value, Location location) {
    super(value.type(), location);
    this.value = value;
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    checkArgument(children.size() == 0);
    return new Dag<>(valueEvaluator(value, location()));
  }
}
