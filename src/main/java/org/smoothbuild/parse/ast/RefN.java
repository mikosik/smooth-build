package org.smoothbuild.parse.ast;

import static org.smoothbuild.util.Throwables.unexpectedCaseExc;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.define.PolyFuncS;
import org.smoothbuild.lang.define.ValS;
import org.smoothbuild.lang.like.Refable;
import org.smoothbuild.lang.type.TKind;

public final class RefN extends GenericAstNode implements ExprN {
  private final String name;
  private Refable referenced;

  public RefN(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void setReferenced(Refable referenced) {
    this.referenced = referenced;
  }

  public Refable referenced() {
    return referenced;
  }

  public Optional<? extends TKind> referencedType() {
    return switch (referenced) {
      case AstNode n -> n.typeO();
      case PolyFuncS f -> f.typeO();
      case ValS v -> v.typeO();
      default -> throw unexpectedCaseExc(referenced);
    };
  }

  @Override
  public String toString() {
    return "RefN(`" + name + "`)";
  }
}
