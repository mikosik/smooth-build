package org.smoothbuild.parse.ast;

import static org.smoothbuild.util.Throwables.unexpectedCaseExc;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.define.FuncS;
import org.smoothbuild.lang.define.ValS;
import org.smoothbuild.lang.like.Refable;
import org.smoothbuild.lang.type.TypeS;

public final class RefN extends MonoExprN {
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

  public Optional<TypeS> referencedType() {
    return switch (referenced) {
      case MonoAstNode n -> n.typeS();
      case FuncS f -> Optional.of(f.type());
      case ValS v -> Optional.of(v.type());
      default -> throw unexpectedCaseExc(referenced);
    };
  }

  @Override
  public String toString() {
    return "RefN(`" + name + "`)";
  }
}
