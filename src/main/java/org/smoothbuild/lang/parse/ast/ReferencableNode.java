package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.base.type.api.Type;

public class ReferencableNode extends NamedNode implements ReferencableLike {
  private final Optional<TypeNode> typeNode;
  private final Optional<ExprNode> body;
  private final Optional<AnnotationNode> annotation;

  public ReferencableNode(Optional<TypeNode> typeNode, String name, Optional<ExprNode> body,
      Optional<AnnotationNode> annotation, Location location) {
    super(name, location);
    this.typeNode = typeNode;
    this.body = body;
    this.annotation = annotation;
  }

  public Optional<TypeNode> typeNode() {
    return typeNode;
  }

  public Optional<ExprNode> body() {
    return body;
  }

  public Optional<AnnotationNode> annotation() {
    return annotation;
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
