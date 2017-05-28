package org.smoothbuild.parse.ast;

import java.util.List;
import java.util.Set;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.parse.Dependency;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class FuncNode extends Node {
  private final Name name;
  private final List<ParamNode> params;
  private final ExprNode expr;
  private final Set<Dependency> dependencies;

  public FuncNode(Name name, List<ParamNode> params, ExprNode expr,
      Set<Dependency> dependencies, CodeLocation location) {
    super(location);
    this.name = name;
    this.params = ImmutableList.copyOf(params);
    this.expr = expr;
    this.dependencies = ImmutableSet.copyOf(dependencies);
  }

  public Name name() {
    return name;
  }

  public List<ParamNode> params() {
    return params;
  }

  public ExprNode expr() {
    return expr;
  }

  public Set<Dependency> dependencies() {
    return dependencies;
  }

  public FuncNode withParams(List<ParamNode> params) {
    return new FuncNode(name, params, expr, dependencies, codeLocation());
  }

  public final boolean equals(Object object) {
    if (!(object instanceof FuncNode)) {
      return false;
    }
    FuncNode that = (FuncNode) object;
    return this.name.equals(that.name);
  }

  public final int hashCode() {
    return name.hashCode();
  }

  public String toString() {
    return "[" + name + ":" + codeLocation() + "]";
  }
}
