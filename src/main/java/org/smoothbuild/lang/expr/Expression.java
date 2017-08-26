package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.function.base.Scope;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Evaluator;

import com.google.common.collect.ImmutableList;

/**
 * Expression in smooth language.
 */
public abstract class Expression {
  private final Type type;
  private final Location location;
  private final ImmutableList<Expression> dependencies;

  public Expression(Type type, List<Expression> dependencies, Location location) {
    this.type = checkNotNull(type);
    this.dependencies = ImmutableList.copyOf(dependencies);
    this.location = checkNotNull(location);
  }

  public Type type() {
    return type;
  }

  public Location location() {
    return location;
  }

  public ImmutableList<Expression> dependencies() {
    return dependencies;
  }

  protected ImmutableList<Evaluator> createDependenciesEvaluator(ValuesDb valuesDb,
      Scope<Evaluator> scope) {
    return dependencies
        .stream()
        .map(e -> e.createEvaluator(valuesDb, scope))
        .collect(ImmutableList.toImmutableList());
  }

  public abstract Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope);
}
