package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.task.base.Evaluator.valueEvaluator;

import java.util.List;

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
  public Evaluator createEvaluator(List<Expression> children, ValuesDb valuesDb,
      Scope<Evaluator> scope) {
    checkArgument(children.size() == 0);
    return valueEvaluator(value, location());
  }
}
