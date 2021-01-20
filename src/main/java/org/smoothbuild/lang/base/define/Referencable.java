package org.smoothbuild.lang.base.define;

import java.util.Optional;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.lang.parse.ast.RefTarget;

public class Referencable extends Defined implements RefTarget {
  public Referencable(Type type, String name, Location location) {
    super(type, name, location);
  }

  public Expression createReferenceExpression(Location location) {
    return new ReferenceExpression(name(), type(), location);
  }

  @Override
  public Optional<Type> inferredType() {
    return Optional.of(type());
  }
}
