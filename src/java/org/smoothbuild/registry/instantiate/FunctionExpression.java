package org.smoothbuild.registry.instantiate;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.lang.function.Type;
import org.smoothbuild.lang.function.exc.FunctionException;

import com.google.common.collect.ImmutableMap;

public class FunctionExpression implements Expression {
  private final ExpressionId id;
  private final Function function;
  private Object result;
  private final ImmutableMap<String, Expression> arguments;

  public FunctionExpression(ExpressionId id, Function function,
      ImmutableMap<String, Expression> arguments) {
    this.id = id;
    this.function = function;
    this.arguments = arguments;
  }

  @Override
  public ExpressionId id() {
    return id;
  }

  @Override
  public Type type() {
    return function.type();
  }

  public void calculate() throws FunctionException {
    result = function.execute(id.resultDir(), arguments);
  }

  public Object result() {
    checkState(result != null, "Cannot return result from not executed function");
    return result;
  }
}
