package org.smoothbuild.parse;

import org.smoothbuild.problem.SourceLocation;

public class Dependency {
  private final SourceLocation location;
  private final String functionName;

  public Dependency(SourceLocation location, String functionName) {
    this.location = location;
    this.functionName = functionName;
  }

  public SourceLocation location() {
    return location;
  }

  public String functionName() {
    return functionName;
  }

  @Override
  public final boolean equals(Object object) {
    if (!(object instanceof Dependency)) {
      return false;
    }
    Dependency that = (Dependency) object;
    return this.functionName.equals(that.functionName);
  }

  @Override
  public final int hashCode() {
    return functionName.hashCode();
  }

  @Override
  public String toString() {
    return "[" + functionName + ":" + location + "]";
  }
}
