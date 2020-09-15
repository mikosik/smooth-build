package org.smoothbuild.lang.expr;

/**
 * Expression in smooth language.
 */
public interface Expression {
  public abstract <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException;
}
