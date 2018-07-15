package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.constructorCallEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class ConstructorCallExpression extends Expression {
  private final Constructor constructor;

  public ConstructorCallExpression(Constructor constructor, Location location) {
    super(constructor.type(), location);
    this.constructor = constructor;
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    return new Dag<>(constructorCallEvaluator(constructor, location()),
        createChildrenEvaluators(children, valuesDb, scope));
  }
}
