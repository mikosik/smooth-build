package org.smoothbuild.lang.base.type;

public class TestedAssignSpecS implements TestedAssignSpec<TestedTS> {
  private final TestedAssignS assignment;
  private final boolean allowed;

  public TestedAssignSpecS(TestedTS target, TestedTS source, boolean allowed) {
    this(new TestedAssignS(target, source), allowed);
  }

  public TestedAssignSpecS(TestedAssign<TestedTS> assignment, boolean allowed) {
    this.assignment = (TestedAssignS) assignment;
    this.allowed = allowed;
  }

  @Override
  public TestedAssign<TestedTS> assignment() {
    return assignment;
  }

  @Override
  public boolean allowed() {
    return allowed;
  }

  public String declarations() {
    return assignment.declarations();
  }

  public String typeDeclarations() {
    return assignment.typeDeclarations();
  }

  @Override
  public String toString() {
    return toStringImpl();
  }
}
