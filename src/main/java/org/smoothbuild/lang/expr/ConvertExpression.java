package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.convertEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class ConvertExpression extends Expression {
  public ConvertExpression(ConcreteType type, Location location) {
    super(type, location);
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    return new Dag<Evaluator>(convertEvaluator((ConcreteType) type(), location()),
        createChildrenEvaluators(children, valuesDb, scope));
  }
}
