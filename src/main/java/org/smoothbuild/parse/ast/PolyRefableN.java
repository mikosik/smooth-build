package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;

public sealed abstract class PolyRefableN extends PolyNamedN implements RefableN
    permits FuncN {
  private final Optional<ObjN> body;
  private final Optional<AnnN> ann;

  public PolyRefableN(String name, Optional<ObjN> body, Optional<AnnN> ann, Loc loc) {
    super(name, loc);
    this.body = body;
    this.ann = ann;
  }

  @Override
  public Optional<ObjN> body() {
    return body;
  }

  @Override
  public Optional<AnnN> ann() {
    return ann;
  }

  @Override
  public final boolean equals(Object object) {
    return object instanceof PolyRefableN that
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
