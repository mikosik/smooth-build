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
  private final ConcreteType type;

  public ConvertExpression(ConcreteType type, Location location) {
    super(location);
    this.type = type;
  }

  @Override
  public Evaluator createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Evaluator> scope) {
    return convertEvaluator(type, evaluators(children, valuesDb, scope), location());
  }
}
