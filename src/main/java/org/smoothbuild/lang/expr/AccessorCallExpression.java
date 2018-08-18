package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.task.base.Evaluator.accessorCallEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.task.base.Evaluator;

public class AccessorCallExpression extends Expression {
  private final Accessor accessor;

  public AccessorCallExpression(Accessor accessor, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.accessor = accessor;
  }

  @Override
  public Evaluator createEvaluator(List<Expression> children, ValuesDb valuesDb,
      Scope<Evaluator> scope) {
    checkArgument(children.size() == 1);
    return accessorCallEvaluator(accessor, evaluators(children, valuesDb, scope), location());
  }
}
