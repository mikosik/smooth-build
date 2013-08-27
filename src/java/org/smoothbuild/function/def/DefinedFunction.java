package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.FunctionSignature;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;

/**
 * Function that is defined completely in Smooth script using Smooth language
 * (as opposed to {@link org.smoothbuild.function.plugin.PluginFunction} which
 * is implemented completely in java language).
 */
public class DefinedFunction extends AbstractFunction {
  private final DefinitionNode root;

  public DefinedFunction(FunctionSignature signature, DefinitionNode root) {
    super(signature);
    this.root = root;
  }

  @Override
  public Expression apply(ExpressionIdFactory idFactory, Map<String, Expression> arguments) {
    checkArgument(arguments.isEmpty(),
        "Defined function cannot have any arguments. (That should change soon!)");
    return root.expression(idFactory);
  }
}
