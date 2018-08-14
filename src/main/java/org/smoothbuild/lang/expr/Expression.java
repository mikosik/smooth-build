package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.task.base.Evaluator.convertEvaluator;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.type.ConcreteType;
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

  public static Dag<Evaluator> convertIfNeeded(ConcreteType type, Dag<Evaluator> evaluator) {
    if (evaluator.elem().type().equals(type)) {
      return evaluator;
    } else {
      return new Dag<>(convertEvaluator(type, evaluator.elem().location()), list(evaluator));
    }
  }

  public static List<ConcreteType> evaluatorTypes(List<Dag<Evaluator>> argumentEvaluators) {
    return map(argumentEvaluators, a -> a.elem().type());
  }

  public static List<Type> parameterTypes(Function function) {
    return map(function.signature().parameters(), p -> p.type());
  }

  public static List<Dag<Evaluator>> evaluators(List<Dag<Expression>> expressions,
      ValuesDb valuesDb, Scope<Dag<Evaluator>> scope) {
    return map(expressions, c -> evaluator(c, valuesDb, scope));
  }

  public static Dag<Evaluator> evaluator(Dag<Expression> expression, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    return expression.elem().createEvaluator(expression.children(), valuesDb, scope);
  }

  public abstract Dag<Evaluator> createEvaluator(List<Dag<Expression>> children,
      ValuesDb valuesDb, Scope<Dag<Evaluator>> scope);
}
