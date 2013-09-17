package org.smoothbuild.parse;

import org.smoothbuild.message.CodeLocation;

public class Dependency {
  private final CodeLocation location;
  private final String functionName;

  public Dependency(CodeLocation location, String functionName) {
    this.location = location;
    this.functionName = functionName;
  }

  public CodeLocation location() {
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
