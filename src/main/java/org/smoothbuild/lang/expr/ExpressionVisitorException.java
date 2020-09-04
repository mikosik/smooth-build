package org.smoothbuild.lang.expr;

public class ExpressionVisitorException extends Exception {
  public ExpressionVisitorException(String message, Throwable e) {
    super(message, e);
  }
}
