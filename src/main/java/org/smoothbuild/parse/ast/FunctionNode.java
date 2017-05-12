package org.smoothbuild.parse.ast;

import java.util.Set;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.parse.Dependency;

import com.google.common.collect.ImmutableSet;

public class FunctionNode extends Node {
  private final Name name;
  private final FunctionContext context;
  private final Set<Dependency> dependencies;

  public FunctionNode(Name name, FunctionContext context, Set<Dependency> dependencies,
      CodeLocation location) {
    super(location);
    this.name = name;
    this.context = context;
    this.dependencies = ImmutableSet.copyOf(dependencies);
  }

  public Name name() {
    return name;
  }

  public FunctionContext context() {
    return context;
  }

  public Set<Dependency> dependencies() {
    return dependencies;
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
    return "[" + name + ":" + codeLocation() + "]";
  }
}
