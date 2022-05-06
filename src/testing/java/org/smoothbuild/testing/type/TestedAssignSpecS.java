package org.smoothbuild.testing.type;

public class TestedAssignSpecS {
  private final TestedAssignS assignment;
  private final boolean allowed;

  public TestedAssignSpecS(TestedTS target, TestedTS source, boolean allowed) {
    this(new TestedAssignS(target, source), allowed);
  }

  public TestedAssignSpecS(TestedAssignS assignment, boolean allowed) {
    this.assignment = assignment;
    this.allowed = allowed;
  }

  public TestedAssignS assignment() {
    return assignment;
  }

  public boolean allowed() {
    return allowed;
  }

  public String declarations() {
    return assignment.declarations();
  }

  public String typeDeclarations() {
    return assignment.typeDeclarations();
  }

  public TestedTS source() {
    return assignment().source();
  }

  public TestedTS target() {
    return assignment().target();
  }

  @Override
  public String toString() {
    return assignment().toString() + " :" + (allowed() ? "allowed" : "illegal");
  }
}
