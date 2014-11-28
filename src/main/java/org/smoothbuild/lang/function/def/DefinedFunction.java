package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.NativeFunction;

/**
 * Smooth function defined in smooth language via smooth expression.
 *
 * @see NativeFunction
 */
public class DefinedFunction<T extends Value> extends AbstractFunction<T> {
  private final Expression<T> root;

  public DefinedFunction(Signature<T> signature, Expression<T> root) {
    super(signature);
    this.root = checkNotNull(root);
  }

  public Expression<T> root() {
    return root;
  }
}
