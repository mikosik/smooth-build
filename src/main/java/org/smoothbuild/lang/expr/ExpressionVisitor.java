package org.smoothbuild.lang.expr;

public interface ExpressionVisitor<C, T> {
  public T visit(C context, FieldReadExpression expression)
      throws ExpressionVisitorException;

  public T visit(C context, ReferenceExpression expression)
      throws ExpressionVisitorException;

  public T visit(C context, ParameterReferenceExpression expression)
      throws ExpressionVisitorException;

  public T visit(C context, CallExpression expression)
      throws ExpressionVisitorException;

  public T visit(C context, ArrayLiteralExpression expression)
      throws ExpressionVisitorException;

  public T visit(C context, BlobLiteralExpression expression)
      throws ExpressionVisitorException;

  public T visit(C context, StringLiteralExpression expression)
      throws ExpressionVisitorException;
}
