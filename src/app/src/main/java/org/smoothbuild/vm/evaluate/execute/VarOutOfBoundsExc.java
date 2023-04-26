package org.smoothbuild.vm.evaluate.execute;

public class VarOutOfBoundsExc extends RuntimeException {
  public VarOutOfBoundsExc(int index, int boundVarsSize) {
    super(createMessage(index, boundVarsSize));
  }

  private static String createMessage(int index, int boundVarsSize) {
    return "Variable index = %d is out of bounds. Bound variables size = %d."
        .formatted(index, boundVarsSize);
  }
}
