package org.smoothbuild.virtualmachine.evaluate.job;

public class ParamRefIndexOutOfBoundsException extends Exception {
  public ParamRefIndexOutOfBoundsException(int index, int boundVarsSize) {
    super(createMessage(index, boundVarsSize));
  }

  private static String createMessage(int index, int boundVarsSize) {
    return "ParamRef index = %d is out of bounds. Bound variables size = %d."
        .formatted(index, boundVarsSize);
  }
}
