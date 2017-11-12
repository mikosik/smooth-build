package org.smoothbuild.lang.function.base;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableList;

public interface Function {
  public Signature signature();

  public Type type();

  public Name name();

  public ImmutableList<Parameter> parameters();

  public Expression createCallExpression(boolean isGenerated, Location location);
}
