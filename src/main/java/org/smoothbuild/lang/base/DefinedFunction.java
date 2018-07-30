package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.TypeChooser;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.util.Dag;

/**
 * Smooth function defined in smooth language via smooth expression.
 *
 * @see NativeFunction
 */
public class DefinedFunction extends Function {
  private final Dag<Expression> definition;

  public DefinedFunction(Signature signature, Location location, Dag<Expression> definition) {
    super(signature, location);
    this.definition = checkNotNull(definition);
  }

  public Dag<Expression> definition() {
    return definition;
  }

  @Override
  public Expression createCallExpression(Type type, TypeChooser evaluatorTypeChooser,
      Location location) {
    return new CallExpression(type, evaluatorTypeChooser, this, location);
  }
}
