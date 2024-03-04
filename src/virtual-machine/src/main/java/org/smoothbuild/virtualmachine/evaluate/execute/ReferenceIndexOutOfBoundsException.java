package org.smoothbuild.virtualmachine.evaluate.execute;

public class ReferenceIndexOutOfBoundsException extends RuntimeException {
  public ReferenceIndexOutOfBoundsException(int index, int boundVarsSize) {
    super(createMessage(index, boundVarsSize));
  }

  private static String createMessage(int index, int boundVarsSize) {
    return "Reference index = %d is out of bounds. Bound variables size = %d."
        .formatted(index, boundVarsSize);
  }
}
