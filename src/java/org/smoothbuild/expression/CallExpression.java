package org.smoothbuild.expression;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.smoothbuild.function.Function;
import org.smoothbuild.function.Type;
import org.smoothbuild.plugin.exc.FunctionException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class CallExpression implements Expression {
  private final ExpressionId id;
  private final Function function;
  private Object result;
  private final ImmutableMap<String, Expression> arguments;

  public CallExpression(ExpressionId id, Function function, Map<String, Expression> arguments) {
    this.id = id;
    this.function = function;
    this.arguments = ImmutableMap.copyOf(arguments);
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
    result = function.execute(id.resultDir(), calculateArguments());
  }

  private ImmutableMap<String, Object> calculateArguments() {
    Builder<String, Object> builder = ImmutableMap.builder();
    for (Map.Entry<String, Expression> entry : arguments.entrySet()) {
      builder.put(entry.getKey(), entry.getValue().result());
    }
    return builder.build();
  }

  public Object result() {
    try {
      calculate();
    } catch (FunctionException e) {
      // TODO fix once calculate is called by some worker
      throw new RuntimeException(e);
    }

    checkState(result != null, "Cannot return result from not executed function");
    return result;
  }
}
