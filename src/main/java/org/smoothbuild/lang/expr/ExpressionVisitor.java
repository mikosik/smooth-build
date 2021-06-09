package org.smoothbuild.lang.expr;

public interface ExpressionVisitor<C, T> {
  public T visit(C context, FieldReadExpression expression);

  public T visit(C context, ReferenceExpression expression);

  public T visit(C context, ParameterReferenceExpression expression);

  public T visit(C context, CallExpression expression);

  public T visit(C context, ArrayLiteralExpression expression);

  public T visit(C context, BlobLiteralExpression expression);

  public T visit(C context, StringLiteralExpression expression);
}
