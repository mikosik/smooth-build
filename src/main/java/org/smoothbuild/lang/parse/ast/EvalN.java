package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.like.EvalLike;
import org.smoothbuild.lang.base.type.impl.TypeS;

public sealed class EvalN extends NamedN implements EvalLike
    permits FuncN, ItemN, ValN {
  private final Optional<TypeN> typeNode;
  private final Optional<ExprN> body;
  private final Optional<AnnN> ann;

  public EvalN(Optional<TypeN> typeNode, String name, Optional<ExprN> body, Optional<AnnN> ann,
      Location location) {
    super(name, location);
    this.typeNode = typeNode;
    this.body = body;
    this.ann = ann;
  }

  public Optional<TypeN> typeNode() {
    return typeNode;
  }

  public Optional<ExprN> body() {
    return body;
  }

  public Optional<AnnN> ann() {
    return ann;
  }

  @Override
  public Optional<TypeS> inferredType() {
    return type();
  }

  @Override
  public final boolean equals(Object object) {
    return object instanceof EvalN that
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
