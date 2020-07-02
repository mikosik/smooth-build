package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.parse.AstVisitor;

import com.google.common.collect.ImmutableList;

public class FuncNode extends ParameterizedNode {
  private final TypeNode typeNode;
  private final List<ParamNode> params;
  private final ExprNode expr;

  public FuncNode(TypeNode typeNode, String name, List<ParamNode> params, ExprNode expr,
      Location location) {
    super(name, location);
    this.typeNode = typeNode;
    this.params = ImmutableList.copyOf(params);
    this.expr = expr;
  }

  public void visitType(AstVisitor astVisitor) {
    if (typeNode != null) {
      astVisitor.visitType(typeNode);
    }
  }

  public boolean hasType() {
    return typeNode != null;
  }

  public TypeNode typeNode() {
    return typeNode;
  }

  public List<ParamNode> params() {
    return params;
  }

  public boolean isNative() {
    return expr == null;
  }

  public void visitExpr(AstVisitor astVisitor) {
    if (expr != null) {
      astVisitor.visitExpr(expr);
    }
  }

  public ExprNode expr() {
    return expr;
  }

  @Override
  public final boolean equals(Object object) {
    if (!(object instanceof FuncNode that)) {
      return false;
    }
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
