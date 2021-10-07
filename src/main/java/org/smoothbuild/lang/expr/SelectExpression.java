package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.api.ItemSignature;
import org.smoothbuild.lang.base.type.api.Type;

public record SelectExpression(ItemSignature field, Expression expression, Location location)
    implements Expression {
  @Override
  public Type type() {
    return field.type();
  }
}
