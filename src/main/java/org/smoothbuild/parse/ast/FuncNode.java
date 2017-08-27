package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.Location;

import com.google.common.collect.ImmutableList;

public class FuncNode extends Node {
  private final Name name;
  private final List<ParamNode> params;
  private final ExprNode expr;

  public FuncNode(Name name, List<ParamNode> params, ExprNode expr, Location location) {
    super(location);
    this.name = name;
    this.params = ImmutableList.copyOf(params);
    this.expr = expr;
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
    return "[" + name + ":" + location() + "]";
  }
}
