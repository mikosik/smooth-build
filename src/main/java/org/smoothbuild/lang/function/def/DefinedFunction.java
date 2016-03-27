package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.lang.expr.DefinedCallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.message.CodeLocation;

/**
 * Smooth function defined in smooth language via smooth expression.
 *
 * @see NativeFunction
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

  public Expression createCallExpression(List<Expression> args, boolean isGenerated,
      CodeLocation codeLocation) {
    checkArgument(args.isEmpty());
    checkArgument(!isGenerated);
    return new DefinedCallExpression(this, codeLocation);
  }
}
