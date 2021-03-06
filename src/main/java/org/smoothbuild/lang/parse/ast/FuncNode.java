package org.smoothbuild.lang.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Native;

public class FuncNode extends CallableNode {
  private final TypeNode typeNode;
  private final ExprNode expr;
  private Native nativ;

  public FuncNode(TypeNode typeNode, String name, List<ItemNode> params, ExprNode expr,
      Location location) {
    super(name, params, location);
    this.typeNode = typeNode;
    this.expr = expr;
  }

  public void visitType(AstVisitor astVisitor) {
    if (typeNode != null) {
      astVisitor.visitType(typeNode);
    }
  }

  public boolean declaresType() {
    return typeNode != null;
  }

  public TypeNode typeNode() {
    return typeNode;
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

  public Native nativ() {
    return nativ;
  }

  public void setNative(Native nativ) {
    this.nativ = nativ;
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof FuncNode that) {
      return this.name().equals(that.name());
    }
    return false;
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
