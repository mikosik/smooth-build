package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.Type;

public class ReferencableNode extends NamedNode implements RefTarget {
  private final Optional<TypeNode> typeNode;
  private final Optional<ExprNode> expr;

  public ReferencableNode(
      Optional<TypeNode> typeNode, String name, Optional<ExprNode> expr, Location location) {
    super(name, location);
    this.typeNode = typeNode;
    this.expr = expr;
  }

  public void visitType(AstVisitor astVisitor) {
    typeNode.ifPresent(astVisitor::visitType);
  }

  public Optional<TypeNode> typeNode() {
    return typeNode;
  }

  public void visitExpr(AstVisitor astVisitor) {
    expr.ifPresent(astVisitor::visitExpr);
  }

  public Optional<ExprNode> expr() {
    return expr;
  }

  @Override
  public Optional<Type> inferredType() {
    return type();
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof ReferencableNode that) {
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
