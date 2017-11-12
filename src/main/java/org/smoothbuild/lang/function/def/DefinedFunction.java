package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.util.Dag;

/**
 * Smooth function defined in smooth language via smooth expression.
 *
 * @see NativeFunction
 */
public class DefinedFunction extends AbstractFunction {
  private final Dag<Expression> definition;

  public DefinedFunction(Signature signature, Dag<Expression> definition) {
    super(signature);
    this.definition = checkNotNull(definition);
  }

  public Dag<Expression> definition() {
    return definition;
  }

  @Override
  public Expression createCallExpression(boolean isGenerated, Location location) {
    checkArgument(!isGenerated);
    return new CallExpression(this, location);
  }
}
