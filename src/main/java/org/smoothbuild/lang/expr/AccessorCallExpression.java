package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.accessorCallEvaluator;

import java.util.List;

import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.task.base.Evaluator;

public class AccessorCallExpression extends Expression {
  private final Accessor accessor;

  public AccessorCallExpression(Accessor accessor, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.accessor = accessor;
  }

  @Override
  public Evaluator createEvaluator(ObjectsDb objectsDb, Scope<Evaluator> scope) {
    return accessorCallEvaluator(accessor, childrenEvaluators(objectsDb, scope), location());
  }
}
