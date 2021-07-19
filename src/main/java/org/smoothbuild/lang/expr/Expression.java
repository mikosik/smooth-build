package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

/**
 * Expression in smooth language.
 */
public interface Expression {
  public Type type();

  public Location location();

  public abstract <C, T> T visit(C context, ExpressionVisitor<C, T> visitor);
}
