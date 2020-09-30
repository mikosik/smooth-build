package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.type.Type;

/**
 * Expression in smooth language.
 */
public interface Expression {
  public Type type();

  public abstract <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException;
}
