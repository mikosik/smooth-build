package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableList;

public abstract class Function {
  private final Signature signature;
  private final Location location;

  public Function(Signature signature, Location location) {
    this.signature = checkNotNull(signature);
    this.location = checkNotNull(location);
  }

  public Signature signature() {
    return signature;
  }

  public Location location() {
    return location;
  }

  public Type type() {
    return signature.type();
  }

  public String name() {
    return signature.name();
  }

  public ImmutableList<Parameter> parameters() {
    return signature.parameters();
  }

  public abstract Expression createCallExpression(boolean isGenerated, Location location);
}
