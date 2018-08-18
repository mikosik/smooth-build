package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.task.base.Evaluator.convertEvaluator;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.task.base.Evaluator;

import com.google.common.collect.ImmutableList;

/**
 * Expression in smooth language.
 */
public abstract class Expression {
  private final ImmutableList<Expression> children;
  private final Location location;

  public Expression(Location location) {
    this(ImmutableList.of(), location);
  }

  public Expression(List<? extends Expression> children, Location location) {
    this.children = ImmutableList.copyOf(children);
    this.location = checkNotNull(location);
  }

  public List<Evaluator> childrenEvaluators(ValuesDb valuesDb, Scope<Evaluator> scope) {
    return map(children, ch -> ch.createEvaluator(valuesDb, scope));
  }

  public Location location() {
    return location;
  }

  public static Evaluator convertIfNeeded(ConcreteType type, Evaluator evaluator) {
    if (evaluator.type().equals(type)) {
      return evaluator;
    } else {
      return convertEvaluator(type, list(evaluator), evaluator.location());
    }
  }

  public static List<ConcreteType> evaluatorTypes(List<Evaluator> argumentEvaluators) {
    return map(argumentEvaluators, a -> a.type());
  }

  public abstract Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope);
}
