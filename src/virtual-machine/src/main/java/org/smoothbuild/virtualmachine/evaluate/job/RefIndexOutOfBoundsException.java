package org.smoothbuild.virtualmachine.evaluate.job;

public class RefIndexOutOfBoundsException extends Exception {
  public RefIndexOutOfBoundsException(int index, int boundVarsSize) {
    super(createMessage(index, boundVarsSize));
  }

  private static String createMessage(int index, int boundVarsSize) {
    return "BRef index (%d) is out of bounds. Bound values count is %d."
        .formatted(index, boundVarsSize);
  }
}
