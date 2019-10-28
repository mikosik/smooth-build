package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.constructorCallEvaluator;

import java.util.List;

import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.task.base.Evaluator;

public class ConstructorCallExpression extends Expression {
  private final Constructor constructor;

  public ConstructorCallExpression(Constructor constructor, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.constructor = constructor;
  }

  @Override
  public Evaluator createEvaluator(ObjectsDb objectsDb, Scope<Evaluator> scope) {
    return constructorCallEvaluator(constructor, childrenEvaluators(objectsDb, scope), location());
  }
}
