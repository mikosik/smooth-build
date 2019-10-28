package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.type.ConcreteType;
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

  public List<Evaluator> childrenEvaluators(ObjectsDb objectsDb, Scope<Evaluator> scope) {
    return map(children, ch -> ch.createEvaluator(objectsDb, scope));
  }

  public Location location() {
    return location;
  }

  public static List<ConcreteType> evaluatorTypes(List<Evaluator> argumentEvaluators) {
    return map(argumentEvaluators, Evaluator::type);
  }

  public abstract Evaluator createEvaluator(ObjectsDb objectsDb, Scope<Evaluator> scope);
}
