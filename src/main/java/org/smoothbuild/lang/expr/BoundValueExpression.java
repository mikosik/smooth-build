package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.task.base.Evaluator;

import com.google.common.collect.ImmutableList;

public class BoundValueExpression extends Expression {
  private final String name;

  public BoundValueExpression(String name, Location location) {
    super(ImmutableList.of(), location);
    this.name = name;
  }

  @Override
  public Evaluator createEvaluator(List<Expression> children, ValuesDb valuesDb,
      Scope<Evaluator> scope) {
    checkArgument(children.size() == 0);
    return scope.get(name);
  }
}
