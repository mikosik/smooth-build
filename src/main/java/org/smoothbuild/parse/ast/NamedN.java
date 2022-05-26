package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.define.Nal;

public sealed class NamedN extends AstNode implements Nal
    permits AnnN, ArgN, RefableN, StructN, TypeN {
  private final String name;

  public NamedN(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof NamedN that
        && this.name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
