package org.smoothbuild.function.plugin;

import java.util.Map;

import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionId;
import org.smoothbuild.function.expr.ExpressionIdFactory;

/**
 * Function that is implemented completely in java (as opposed to
 * {@link org.smoothbuild.function.plugin.PluginFunction} which is defined in
 * Smooth script using Smooth language).
 */
public class PluginFunction extends AbstractFunction {
  private final PluginInvoker pluginInvoker;

  public PluginFunction(Signature signature, PluginInvoker pluginInvoker) {
    super(signature);
    this.pluginInvoker = pluginInvoker;
  }

  @Override
  public Expression apply(ExpressionIdFactory idFactory, Map<String, Expression> arguments) {
    ExpressionId id = idFactory.createId(name().full());
    return new PluginFunctionExpression(id, type(), pluginInvoker, arguments);
  }
}
