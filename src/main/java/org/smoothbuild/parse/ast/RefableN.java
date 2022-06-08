package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.like.Refable;

/**
 * Referencable.
 */
public sealed abstract class RefableN extends MonoNamedN implements Refable
    permits ItemN, RefableObjN {
  private final Optional<ObjN> body;
  private final Optional<AnnN> ann;

  public RefableN(String name, Optional<ObjN> body, Optional<AnnN> ann, Loc loc) {
    super(name, loc);
    this.body = body;
    this.ann = ann;
  }

  public abstract Optional<TypeN> evalTN();

  public Optional<ObjN> body() {
    return body;
  }

  public Optional<AnnN> ann() {
    return ann;
  }

  @Override
  public final boolean equals(Object object) {
    return object instanceof RefableN that
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
