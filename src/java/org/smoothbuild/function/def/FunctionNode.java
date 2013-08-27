package org.smoothbuild.function.def;

import java.util.Map;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class FunctionNode implements DefinitionNode {
  private final Function function;
  private final ImmutableMap<String, DefinitionNode> args;

  public FunctionNode(Function function, Map<String, DefinitionNode> args) {
    this.function = function;
    this.args = ImmutableMap.copyOf(args);
  }

  @Override
  public Type type() {
    return function.type();
  }

  @Override
  public Expression expression(ExpressionIdFactory idFactory) {
    return function.apply(idFactory, calculateArgs(idFactory));
  }

  private Map<String, Expression> calculateArgs(ExpressionIdFactory idFactory) {
    Builder<String, Expression> builder = ImmutableMap.builder();
    for (Map.Entry<String, DefinitionNode> entry : args.entrySet()) {
      String argName = entry.getKey();
      Expression expression = entry.getValue().expression(idFactory);
      builder.put(argName, expression);
    }
    return builder.build();
  }
}
