package org.smoothbuild.virtualmachine.evaluate.execute;

public class VarOutOfBoundsException extends RuntimeException {
  public VarOutOfBoundsException(int index, int boundVarsSize) {
    super(createMessage(index, boundVarsSize));
  }

  private static String createMessage(int index, int boundVarsSize) {
    return "Variable index = %d is out of bounds. Bound variables size = %d."
        .formatted(index, boundVarsSize);
  }
}
