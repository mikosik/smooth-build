package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.Type;
import org.smoothbuild.lang.function.exc.FunctionException;

public interface Expression {

  public ExpressionId id();

  public Type type();

  public void execute() throws FunctionException;

  public Object result();
}
