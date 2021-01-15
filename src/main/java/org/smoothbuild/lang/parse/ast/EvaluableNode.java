package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.Type;

public class EvaluableNode extends NamedNode implements RefTarget {
  private final TypeNode typeNode;
  private final ExprNode expr;

  public EvaluableNode(TypeNode typeNode, String name, ExprNode expr, Location location) {
    super(name, location);
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

  @Override
  public Optional<Type> inferredType() {
    return type();
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof EvaluableNode that) {
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
