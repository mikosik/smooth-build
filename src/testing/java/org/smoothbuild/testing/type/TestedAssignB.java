package org.smoothbuild.testing.type;

public record TestedAssignB(TestedTB target, TestedTB source) {
  @Override
  public String toString() {
    return target().name() + " <- " + source().name();
  }
}
