package org.smoothbuild.parse.ast;

import static org.smoothbuild.util.Throwables.unexpectedCaseExc;

import java.util.Optional;

import org.smoothbuild.lang.define.DefinedS;
import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.like.EvalLike;
import org.smoothbuild.lang.type.impl.TypeS;

public final class RefN extends ExprN {
  private final String name;
  private EvalLike referenced;

  public RefN(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void setReferenced(EvalLike referenced) {
    this.referenced = referenced;
  }

  public EvalLike referenced() {
    return referenced;
  }

  public Optional<TypeS> referencedType() {
    return switch (referenced) {
      case Node n -> n.type();
      case DefinedS d -> Optional.of(d.type());
      default -> throw unexpectedCaseExc(referenced);
    };
  }

  @Override
  public String toString() {
    return RefN.class.getName() + "(" + name + ")";
  }
}
