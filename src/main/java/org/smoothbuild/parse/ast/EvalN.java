package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.like.EvalLike;

public sealed abstract class EvalN extends NamedN implements EvalLike
    permits FuncN, ItemN, ValN {
  private final Optional<TypeN> evalT;
  private final Optional<ExprN> body;
  private final Optional<AnnN> ann;

  public EvalN(Optional<TypeN> evalT, String name, Optional<ExprN> body, Optional<AnnN> ann,
      Loc loc) {
    super(name, loc);
    this.evalT = evalT;
    this.body = body;
    this.ann = ann;
  }

  public Optional<TypeN> evalT() {
    return evalT;
  }

  public Optional<ExprN> body() {
    return body;
  }

  public Optional<AnnN> ann() {
    return ann;
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
    return "[" + name() + ":" + loc() + "]";
  }
}