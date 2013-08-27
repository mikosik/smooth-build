package org.smoothbuild.function.plugin;

import java.util.Map;

import org.smoothbuild.expression.CallExpression;
import org.smoothbuild.expression.Expression;
import org.smoothbuild.expression.ExpressionId;
import org.smoothbuild.expression.ExpressionIdFactory;
import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.FunctionSignature;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;

import com.google.common.collect.ImmutableMap;

public class PluginFunction extends AbstractFunction {
  private final FunctionInvoker functionInvoker;

  public PluginFunction(FunctionSignature signature, FunctionInvoker functionInvoker) {
    super(signature);
    this.functionInvoker = functionInvoker;
  }

  @Override
  public Expression apply(ExpressionIdFactory idFactory, Map<String, Expression> arguments) {
    ExpressionId id = idFactory.createId(name().full());
    return new CallExpression(id, this, arguments);
  }

  @Override
  public Object execute(Path resultDir, ImmutableMap<String, Object> arguments)
      throws FunctionException {
    return functionInvoker.invoke(resultDir, arguments);
  }
}
