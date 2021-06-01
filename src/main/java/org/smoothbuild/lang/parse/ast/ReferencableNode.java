package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.ImplementedBy;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.base.type.Type;

public class ReferencableNode extends NamedNode implements ReferencableLike {
  private final Optional<TypeNode> typeNode;
  private final Optional<ExprNode> expr;
  private final Optional<ImplementedBy> implementedBy;

  public ReferencableNode(
      Optional<TypeNode> typeNode, String name, Optional<ExprNode> expr,
      Optional<ImplementedBy> implementedBy, Location location) {
    super(name, location);
    this.typeNode = typeNode;
    this.expr = expr;
    this.implementedBy = implementedBy;
  }

  public Optional<TypeNode> typeNode() {
    return typeNode;
  }

  public Optional<ExprNode> expr() {
    return expr;
  }

  public Optional<ImplementedBy> implementedBy() {
    return implementedBy;
  }

  @Override
  public Optional<Type> inferredType() {
    return type();
  }

  @Override
  public final boolean equals(Object object) {
    return object instanceof ReferencableNode that
        && this.name().equals(that.name());
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
