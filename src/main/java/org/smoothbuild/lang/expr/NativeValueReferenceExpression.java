package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.NativeValue;

public class NativeValueReferenceExpression extends Expression {
  private final NativeValue nativeValue;

  public NativeValueReferenceExpression(NativeValue nativeValue, Location location) {
    super(location);
    this.nativeValue = nativeValue;
  }

  public String name() {
    return nativeValue.name();
  }

  public NativeValue nativeValue() {
    return nativeValue;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
