package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;

public sealed abstract class MonoRefableP extends MonoNamedP implements RefableP
    permits ItemP, ValP {
  private final Optional<ObjP> body;
  private final Optional<AnnP> ann;

  public MonoRefableP(String name, Optional<ObjP> body, Optional<AnnP> ann, Loc loc) {
    super(name, loc);
    this.body = body;
    this.ann = ann;
  }

  @Override
  public Optional<ObjP> body() {
    return body;
  }

  @Override
  public Optional<AnnP> ann() {
    return ann;
  }

  @Override
  public final boolean equals(Object object) {
    return object instanceof MonoRefableP that
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
