package org.smoothbuild.lang.base.type;

public record TestedAssignH(TestedTH target, TestedTH source) implements TestedAssign<TestedTH> {
  @Override
  public String toString() {
    return target().name() + " <- " + source().name();
  }
}
