package org.smoothbuild.function.plugin;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionId;
import org.smoothbuild.plugin.exc.FunctionException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class PluginFunctionExpression implements Expression {
  private final ExpressionId id;
  private final Type type;
  private final PluginInvoker pluginInvoker;
  private final ImmutableMap<String, Expression> arguments;
  private Object result;

  public PluginFunctionExpression(ExpressionId id, Type type, PluginInvoker pluginInvoker,
      Map<String, Expression> arguments) {
    this.id = id;
    this.type = type;
    this.pluginInvoker = pluginInvoker;
    this.arguments = ImmutableMap.copyOf(arguments);
  }

  @Override
  public ExpressionId id() {
    return id;
  }

  @Override
  public Type type() {
    return type;
  }

  public void calculate() throws FunctionException {
    result = pluginInvoker.invoke(id.resultDir(), calculateArguments());
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
