package org.smoothbuild.testing.type;

public class TestedAssignSpecB implements TestedAssignSpec<TestedTB> {
  private final TestedAssignB assignment;
  private final boolean allowed;

  public TestedAssignSpecB(TestedTB target, TestedTB source, boolean allowed) {
    this(new TestedAssignB(target, source), allowed);
  }

  public TestedAssignSpecB(TestedAssign<TestedTB> assignment, boolean allowed) {
    this.assignment = (TestedAssignB) assignment;
    this.allowed = allowed;
  }

  @Override
  public TestedAssign<TestedTB> assignment() {
    return assignment;
  }

  @Override
  public boolean allowed() {
    return allowed;
  }

  @Override
  public TestedTB source() {
    return assignment().source();
  }

  @Override
  public TestedTB target() {
    return assignment().target();
  }

  @Override
  public String toString() {
    return assignment().toString() + " :" + (allowed() ? "allowed" : "illegal");
  }
}
