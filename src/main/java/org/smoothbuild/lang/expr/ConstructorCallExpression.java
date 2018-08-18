package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.constructorCallEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.task.base.Evaluator;

public class ConstructorCallExpression extends Expression {
  private final Constructor constructor;

  public ConstructorCallExpression(Constructor constructor, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.constructor = constructor;
  }

  @Override
  public Evaluator createEvaluator(List<Expression> children, ValuesDb valuesDb,
      Scope<Evaluator> scope) {
    return constructorCallEvaluator(constructor, evaluators(children, valuesDb, scope), location());
  }
}
