package org.smoothbuild.parse.ast;

import static org.smoothbuild.util.Throwables.unexpectedCaseExc;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.define.RefableObjS;
import org.smoothbuild.lang.like.Refable;
import org.smoothbuild.lang.type.TypeS;

public final class RefN extends ExprN {
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
      case AstNode n -> n.type();
      case RefableObjS d -> Optional.of(d.type());
      default -> throw unexpectedCaseExc(referenced);
    };
  }

  @Override
  public String toString() {
    return RefN.class.getName() + "(" + name + ")";
  }
}
