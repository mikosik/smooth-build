package org.smoothbuild.virtualmachine.bytecode.expr.exc;

public class DecodeExprException extends BExprDbException {
  public DecodeExprException(String message, Throwable cause) {
    super(message, cause);
  }

  public DecodeExprException(String message) {
    super(message);
  }

  public static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }
}
