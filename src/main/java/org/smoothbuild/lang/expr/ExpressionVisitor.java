package org.smoothbuild.lang.expr;

public class ExpressionVisitor<T> {
  public T visit(FieldReadExpression expression) throws ExpressionVisitorException {
    return null;
  }

  public T visit(ArrayLiteralExpression expression) throws ExpressionVisitorException {
    return null;
  }

  public T visit(DefinedValueReferenceExpression expression) throws ExpressionVisitorException {
    return null;
  }

  public T visit(NativeValueReferenceExpression expression) throws ExpressionVisitorException {
    return null;
  }

  public T visit(ParameterReferenceExpression expression) throws ExpressionVisitorException {
    return null;
  }

  public T visit(ConstructorCallExpression expression) throws ExpressionVisitorException {
    return null;
  }

  public T visit(DefinedCallExpression expression) throws ExpressionVisitorException {
    return null;
  }

  public T visit(NativeCallExpression expression) throws ExpressionVisitorException {
    return null;
  }

  public T visit(StringLiteralExpression expression) throws ExpressionVisitorException {
    return null;
  }

  public T visit(BlobLiteralExpression expression) throws ExpressionVisitorException {
    return null;
  }

  public T visit(ConvertExpression convertExpression) throws ExpressionVisitorException {
    return null;
  }
}
