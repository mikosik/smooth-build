package org.smoothbuild.parse;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.CodeLocation;

public class Dependency {
  private final CodeLocation location;
  private final Name functionName;

  public Dependency(CodeLocation location, Name functionName) {
    this.location = location;
    this.functionName = functionName;
  }

  public CodeLocation location() {
    return location;
  }

  public Name functionName() {
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
