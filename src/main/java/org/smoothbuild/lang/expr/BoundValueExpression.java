package org.smoothbuild.lang.expr;

import static java.util.Arrays.asList;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Scope;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Evaluator;

public class BoundValueExpression extends Expression {
  private final Name name;

  public BoundValueExpression(Type type, Name name, Location location) {
    super(type, asList(), location);
    this.name = name;
  }

  @Override
  public Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope) {
    return scope.get(name);
  }
}
