package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.NativeFunctionLegacy;

/**
 * Smooth function defined in smooth language via smooth expression.
 *
 * @see NativeFunctionLegacy
 */
public class DefinedFunction extends AbstractFunction {
  private final Expression root;

  public DefinedFunction(Signature signature, Expression root) {
    super(signature);
    this.root = checkNotNull(root);
  }

  public Expression root() {
    return root;
  }
}
