package org.smoothbuild.lang.expr;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.task.base.Evaluator;

public class BoundValueExpression extends Expression {
  private final String name;

  public BoundValueExpression(String name, Location location) {
    super(location);
    this.name = name;
  }

  @Override
  public Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope) {
    return scope.get(name);
  }
}
