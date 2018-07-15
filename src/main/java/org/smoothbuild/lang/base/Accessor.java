package org.smoothbuild.lang.base;

import org.smoothbuild.lang.expr.AccessorCallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.message.Location;

public class Accessor extends Function {
  private final String fieldName;

  public Accessor(Signature signature, String fieldName, Location location) {
    super(signature, location);
    this.fieldName = fieldName;
  }

  public String fieldName() {
    return fieldName;
  }

  @Override
  public Expression createCallExpression(Location location) {
    return new AccessorCallExpression(this, location);
  }
}
