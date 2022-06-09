package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.ArrayTS;

import com.google.common.collect.ImmutableList;

public record OrderS(ArrayTS type, ImmutableList<MonoObjS> elems, Loc loc) implements MonoExprS {
  @Override
  public String name() {
    return "[]";
  }
}
