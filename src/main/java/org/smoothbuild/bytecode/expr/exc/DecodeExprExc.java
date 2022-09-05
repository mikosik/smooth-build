package org.smoothbuild.bytecode.expr.exc;


public class DecodeExprExc extends BytecodeDbExc {
  public DecodeExprExc(String message, Throwable cause) {
    super(message, cause);
  }

  public DecodeExprExc(String message) {
    super(message);
  }

  public static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }
}
