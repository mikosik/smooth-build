package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.valueEvaluator;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Evaluator;

import com.google.common.collect.ImmutableList;

public class LiteralExpression extends Expression {
  private final Value value;

  public LiteralExpression(Value value, Location location) {
    super(ImmutableList.of(), location);
    this.value = value;
  }

  @Override
  public Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope) {
    return valueEvaluator(value, location());
  }
}
