package org.smoothbuild.lang.obj;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.ArrayTS;

import com.google.common.collect.ImmutableList;

public record OrderS(ArrayTS type, ImmutableList<ObjS> elems, Loc loc) implements ExprS {
  @Override
  public String name() {
    return "[]";
  }
}
