package org.smoothbuild.lang.expr;

public interface ExpressionVisitor<T> {
  public T visit(FieldReadExpression expression) throws ExpressionVisitorException;

  public T visit(ValueReferenceExpression expression) throws ExpressionVisitorException;

  public T visit(ParameterReferenceExpression expression) throws ExpressionVisitorException;

  public T visit(CallExpression expression) throws ExpressionVisitorException;

  public T visit(ArrayLiteralExpression expression) throws ExpressionVisitorException;

  public T visit(BlobLiteralExpression expression) throws ExpressionVisitorException;

  public T visit(StringLiteralExpression expression) throws ExpressionVisitorException;
}
