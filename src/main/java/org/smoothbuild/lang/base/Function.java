package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.parse.ast.Named;

import com.google.common.collect.ImmutableList;

public abstract class Function implements Named {
  private final Signature signature;
  private final Location location;

  public Function(Signature signature, Location location) {
    this.signature = checkNotNull(signature);
    this.location = checkNotNull(location);
  }

  public Signature signature() {
    return signature;
  }

  @Override
  public Location location() {
    return location;
  }

  public ConcreteType type() {
    return signature.type();
  }

  @Override
  public String name() {
    return signature.name();
  }

  public ImmutableList<Parameter> parameters() {
    return signature.parameters();
  }

  public Expression createCallExpression(Location location) {
    return createCallExpression(type(), location);
  }

  public abstract Expression createCallExpression(ConcreteType type, Location location);
}
