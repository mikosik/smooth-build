package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.MonoTS;

public record MonoizeS(MonoTS type, PolyRefS funcRef, Loc loc) implements MonoExprS {
  @Override
  public String name() {
    return "<" + type + ">";
  }
}
