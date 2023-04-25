package org.smoothbuild.vm.evaluate.execute;

public class ReferenceOutOfBoundsExc extends RuntimeException {
  public ReferenceOutOfBoundsExc(int index, int environmentSize) {
    super(createMessage(index, environmentSize));
  }

  private static String createMessage(int index, int environmentSize) {
    return "Reference index = %d is out of bounds. Environment size = %d."
        .formatted(index, environmentSize);
  }
}
