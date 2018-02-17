package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.message.Location;

import com.google.common.collect.ImmutableList;

public class FuncNode extends Node implements Named {
  private final TypeNode type;
  private final String name;
  private final List<ParamNode> params;
  private final ExprNode expr;

  public FuncNode(TypeNode type, String name, List<ParamNode> params, ExprNode expr,
      Location location) {
    super(location);
    this.type = type;
    this.name = name;
    this.params = ImmutableList.copyOf(params);
    this.expr = expr;
  }

  public boolean hasType() {
    return type != null;
  }

  public TypeNode type() {
    return type;
  }

  @Override
  public String name() {
    return name;
  }

  public List<ParamNode> params() {
    return params;
  }

  public boolean isNative() {
    return expr == null;
  }

  public ExprNode expr() {
    return expr;
  }

  @Override
  public final boolean equals(Object object) {
    if (!(object instanceof FuncNode)) {
      return false;
    }
    FuncNode that = (FuncNode) object;
    return this.name().equals(that.name());
  }

  @Override
  public final int hashCode() {
    return name().hashCode();
  }

  @Override
  public String toString() {
    return "[" + name() + ":" + location() + "]";
  }
}
