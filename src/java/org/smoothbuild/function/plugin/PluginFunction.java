package org.smoothbuild.function.plugin;

import java.util.Map;

import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.FunctionSignature;
import org.smoothbuild.function.expr.CallExpression;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionId;
import org.smoothbuild.function.expr.ExpressionIdFactory;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;

import com.google.common.collect.ImmutableMap;

/**
 * Function that is implemented completely in java (as opposed to one defined in
 * smooth script using smooth language).
 */
public class PluginFunction extends AbstractFunction {
  private final PluginInvoker pluginInvoker;

  public PluginFunction(FunctionSignature signature, PluginInvoker pluginInvoker) {
    super(signature);
    this.pluginInvoker = pluginInvoker;
  }

  @Override
  public Expression apply(ExpressionIdFactory idFactory, Map<String, Expression> arguments) {
    ExpressionId id = idFactory.createId(name().full());
    return new CallExpression(id, this, arguments);
  }

  @Override
  public Object execute(Path resultDir, ImmutableMap<String, Object> arguments)
      throws FunctionException {
    return pluginInvoker.invoke(resultDir, arguments);
  }
}
