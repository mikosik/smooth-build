package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.task.base.Evaluator.accessorCallEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class AccessorCallExpression extends Expression {
  private final Accessor accessor;

  public AccessorCallExpression(Accessor accessor, Location location) {
    super(accessor.type(), location);
    this.accessor = accessor;
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    checkArgument(children.size() == 1);
    return new Dag<>(accessorCallEvaluator(accessor, location()),
        createChildrenEvaluators(children, valuesDb, scope));
  }
}
