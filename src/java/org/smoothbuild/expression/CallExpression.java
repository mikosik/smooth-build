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
  private final ImmutableMap<String, Expression> argumentProviders;

  public CallExpression(ExpressionId id, Function function,
      Map<String, Expression> argumentProviders) {
    this.id = id;
    this.function = function;
    this.argumentProviders = ImmutableMap.copyOf(argumentProviders);
  }

  @Override
  public ExpressionId id() {
    return id;
  }

  @Override
  public Type type() {
    return function.signature().type();
  }

  public void calculate() throws FunctionException {
    result = function.execute(id.resultDir(), calculateArguments());
  }

  private ImmutableMap<String, Object> calculateArguments() {
    Builder<String, Object> builder = ImmutableMap.builder();
    for (Map.Entry<String, Expression> entry : argumentProviders.entrySet()) {
      builder.put(entry.getKey(), entry.getValue().result());
    }
    return builder.build();
  }

  public Object result() {
    checkState(result != null, "Cannot return result from not executed function");
    return result;
  }
}
