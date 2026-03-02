package org.smoothbuild.virtualmachine.evaluate.job;

public class RefIndexOutOfBoundsException extends Exception {
  public RefIndexOutOfBoundsException(int index, int boundVarsSize) {
    super(createMessage(index, boundVarsSize));
  }

  private static String createMessage(int index, int boundVarsSize) {
    return "Ref index = %d is out of bounds. Bound variables size = %d."
        .formatted(index, boundVarsSize);
  }
}
