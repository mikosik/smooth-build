package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.like.EvaluableLike;
import org.smoothbuild.lang.base.type.impl.TypeS;

public sealed class EvaluableNode extends NamedNode implements EvaluableLike
    permits FunctionNode, ItemNode, ValueNode {
  private final Optional<TypeNode> typeNode;
  private final Optional<ExprNode> body;
  private final Optional<AnnotationNode> annotation;

  public EvaluableNode(Optional<TypeNode> typeNode, String name, Optional<ExprNode> body,
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
  public Optional<TypeS> inferredType() {
    return type();
  }

  @Override
  public final boolean equals(Object object) {
    return object instanceof EvaluableNode that
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
