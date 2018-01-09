package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.convertFromNothingEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.function.base.Scope;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class ConvertFromNothingExpression extends Expression {
  public ConvertFromNothingExpression(Type type, Location location) {
    super(type, location);
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    return new Dag<Evaluator>(convertFromNothingEvaluator(type(), location()),
        createChildrenEvaluators(children, valuesDb, scope));
  }
}
