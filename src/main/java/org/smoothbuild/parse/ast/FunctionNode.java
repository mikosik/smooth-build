package org.smoothbuild.parse.ast;

import java.util.List;
import java.util.Set;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.parse.Dependency;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class FunctionNode extends Node {
  private final Name name;
  private final List<ParamNode> params;
  private final FunctionContext context;
  private final Set<Dependency> dependencies;

  public FunctionNode(Name name, List<ParamNode> params, FunctionContext context,
      Set<Dependency> dependencies, CodeLocation location) {
    super(location);
    this.name = name;
    this.params = ImmutableList.copyOf(params);
    this.context = context;
    this.dependencies = ImmutableSet.copyOf(dependencies);
  }

  public Name name() {
    return name;
  }

  public List<ParamNode> params() {
    return params;
  }

  public FunctionContext context() {
    return context;
  }

  public Set<Dependency> dependencies() {
    return dependencies;
  }

  public FunctionNode withParams(List<ParamNode> params) {
    return new FunctionNode(name, params, context, dependencies, codeLocation());
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
