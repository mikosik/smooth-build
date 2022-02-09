package org.smoothbuild.testing.type;

public record TestedAssignB(TestedTB target, TestedTB source) implements TestedAssign<TestedTB> {
  @Override
  public String toString() {
    return target().name() + " <- " + source().name();
  }
}
