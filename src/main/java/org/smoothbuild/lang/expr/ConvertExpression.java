package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.convertEvaluator;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.task.base.Evaluator;

import com.google.common.collect.ImmutableList;

public class ConvertExpression extends Expression {
  private final ConcreteType type;

  public ConvertExpression(ConcreteType type, Expression expression, Location location) {
    super(ImmutableList.of(expression), location);
    this.type = type;
  }

  @Override
  public Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope) {
    return convertEvaluator(type, childrenEvaluators(valuesDb, scope), location());
  }
}
