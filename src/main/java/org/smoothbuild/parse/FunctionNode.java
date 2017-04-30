package org.smoothbuild.parse;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;

public class FunctionNode {
  private final Name name;
  private final FunctionContext context;
  private final CodeLocation location;

  public FunctionNode(Name name, FunctionContext context, CodeLocation location) {
    this.name = name;
    this.context = context;
    this.location = location;
  }

  public Name name() {
    return name;
  }

  public FunctionContext context() {
    return context;
  }

  public CodeLocation location() {
    return location;
  }

  public final boolean equals(Object object) {
    if (!(object instanceof FunctionNode)) {
      return false;
    }
    FunctionNode that = (FunctionNode) object;
    return this.name.equals(that.name);
  }

  public final int hashCode() {
    return name.hashCode();
  }

  public String toString() {
    return "[" + name + ":" + location + "]";
  }
}
