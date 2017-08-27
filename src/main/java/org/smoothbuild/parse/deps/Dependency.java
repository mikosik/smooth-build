package org.smoothbuild.parse.deps;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.Location;

public class Dependency {
  private final Location location;
  private final Name functionName;

  public Dependency(Location location, Name functionName) {
    this.location = location;
    this.functionName = functionName;
  }

  public Location location() {
    return location;
  }

  public Name functionName() {
    return functionName;
  }

  public final boolean equals(Object object) {
    if (!(object instanceof Dependency)) {
      return false;
    }
    Dependency that = (Dependency) object;
    return this.functionName.equals(that.functionName);
  }

  public final int hashCode() {
    return functionName.hashCode();
  }

  public String toString() {
    return "[" + functionName + ":" + location + "]";
  }
}
