package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.expr.DefinedValueReferenceExpression;
import org.smoothbuild.lang.expr.Expression;

/**
 * Smooth Value defined in smooth language via smooth expression.
 *
 * @see NativeValue
 */
public class DefinedValue extends Value {
  private final Expression body;

  public DefinedValue(ConcreteType type, String name, Expression body, Location location) {
    super(type, name, location);
    this.body = checkNotNull(body);
  }

  public Expression body() {
    return body;
  }

  @Override
  public Expression createReferenceExpression(Location location) {
    return new DefinedValueReferenceExpression(name(), location);
  }
}


