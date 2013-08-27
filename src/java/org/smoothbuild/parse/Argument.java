package org.smoothbuild.parse;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.problem.SourceLocation;

public class Argument {
  private final String name;
  private final Expression expression;
  private final SourceLocation sourceLocation;

  public Argument(String name, Expression expression, SourceLocation sourceLocation) {
    this.name = name;
    this.expression = checkNotNull(expression);
    this.sourceLocation = checkNotNull(sourceLocation);
  }

  public String name() {
    return name;
  }

  public Expression expression() {
    return expression;
  }

  public SourceLocation sourceLocation() {
    return sourceLocation;
  }

  public boolean isExplicit() {
    return name != null;
  }
}
