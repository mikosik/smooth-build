package org.smoothbuild.lang.base.type;

public class TestedAssignSpecH implements TestedAssignSpec<TestedTH> {
  private final TestedAssignH assignment;
  private final boolean allowed;

  public TestedAssignSpecH(TestedTH target, TestedTH source, boolean allowed) {
    this(new TestedAssignH(target, source), allowed);
  }

  public TestedAssignSpecH(TestedAssign<TestedTH> assignment, boolean allowed) {
    this.assignment = (TestedAssignH) assignment;
    this.allowed = allowed;
  }

  @Override
  public TestedAssign<TestedTH> assignment() {
    return assignment;
  }

  @Override
  public boolean allowed() {
    return allowed;
  }

  @Override
  public String toString() {
    return toStringImpl();
  }
}
