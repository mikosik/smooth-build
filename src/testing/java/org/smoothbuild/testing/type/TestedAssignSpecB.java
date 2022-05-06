package org.smoothbuild.testing.type;

public class TestedAssignSpecB {
  private final TestedAssignB assignment;
  private final boolean allowed;

  public TestedAssignSpecB(TestedTB target, TestedTB source, boolean allowed) {
    this(new TestedAssignB(target, source), allowed);
  }

  public TestedAssignSpecB(TestedAssignB assignment, boolean allowed) {
    this.assignment = assignment;
    this.allowed = allowed;
  }

  public TestedAssignB assignment() {
    return assignment;
  }

  public boolean allowed() {
    return allowed;
  }

  public TestedTB source() {
    return assignment().source();
  }

  public TestedTB target() {
    return assignment().target();
  }

  @Override
  public String toString() {
    return assignment().toString() + " :" + (allowed() ? "allowed" : "illegal");
  }
}
