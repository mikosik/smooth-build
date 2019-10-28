package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.valueEvaluator;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.task.base.Evaluator;

public class LiteralExpression extends Expression {
  private final SObject object;

  public LiteralExpression(SObject object, Location location) {
    super(location);
    this.object = object;
  }

  @Override
  public Evaluator createEvaluator(ObjectsDb objectsDb, Scope<Evaluator> scope) {
    return valueEvaluator(object, location());
  }
}
