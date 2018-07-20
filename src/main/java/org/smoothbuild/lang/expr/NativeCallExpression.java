package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.nativeCallEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class NativeCallExpression extends Expression {
  private final NativeFunction nativeFunction;

  public NativeCallExpression(ConcreteType type, NativeFunction nativeFunction, Location location) {
    super(type, location);
    this.nativeFunction = nativeFunction;
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    return new Dag<>(nativeCallEvaluator(type(), nativeFunction, location()),
        createChildrenEvaluators(children, valuesDb, scope));
  }
}
