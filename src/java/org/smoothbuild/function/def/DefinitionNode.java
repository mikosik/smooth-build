package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;

public interface DefinitionNode {
  public Type type();

  public Expression expression(ExpressionIdFactory idFactory);
}
