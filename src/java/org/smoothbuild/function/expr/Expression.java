package org.smoothbuild.function.expr;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.plugin.exc.FunctionException;

public interface Expression {

  public ExpressionId id();

  public Type type();

  public void calculate() throws FunctionException;

  public Object result();
}
