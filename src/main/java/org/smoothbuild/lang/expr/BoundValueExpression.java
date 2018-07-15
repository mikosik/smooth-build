package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class BoundValueExpression extends Expression {
  private final String name;

  public BoundValueExpression(Type type, String name, Location location) {
    super(type, location);
    this.name = name;
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    checkArgument(children.size() == 0);
    return scope.get(name);
  }
}
