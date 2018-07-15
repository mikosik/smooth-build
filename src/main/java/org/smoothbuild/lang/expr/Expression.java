package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

/**
 * Expression in smooth language.
 */
public abstract class Expression {
  private final Type type;
  private final Location location;

  public Expression(Type type, Location location) {
    this.type = checkNotNull(type);
    this.location = checkNotNull(location);
  }

  public Type type() {
    return type;
  }

  public Location location() {
    return location;
  }

  public static List<Dag<Evaluator>> createChildrenEvaluators(List<Dag<Expression>> children,
      ValuesDb valuesDb, Scope<Dag<Evaluator>> scope) {
    return children
        .stream()
        .map(c -> c.elem().createEvaluator(c.children(), valuesDb, scope))
        .collect(toImmutableList());
  }

  public abstract Dag<Evaluator> createEvaluator(List<Dag<Expression>> children,
      ValuesDb valuesDb, Scope<Dag<Evaluator>> scope);
}
