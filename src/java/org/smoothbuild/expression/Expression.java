package org.smoothbuild.expression;

import org.smoothbuild.lang.function.Type;
import org.smoothbuild.lang.function.exc.FunctionException;

public interface Expression {

  public ExpressionId id();

  public Type type();

  public void calculate() throws FunctionException;

  public Object result();
}
