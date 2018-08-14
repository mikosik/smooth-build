package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.expr.DefinedCallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeChooser;
import org.smoothbuild.util.Dag;

/**
 * Smooth function defined in smooth language via smooth expression.
 *
 * @see NativeFunction
 */
public class DefinedFunction extends Function {
  private final Dag<Expression> body;

  public DefinedFunction(Signature signature, Location location, Dag<Expression> body) {
    super(signature, location);
    this.body = checkNotNull(body);
  }

  public Dag<Expression> body() {
    return body;
  }

  @Override
  public Expression createCallExpression(Type type, TypeChooser<ConcreteType> typeChooser,
      Location location) {
    return new DefinedCallExpression(type, typeChooser, this, location);
  }
}
