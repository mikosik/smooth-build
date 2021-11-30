package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;

import com.google.common.collect.ImmutableList;

public record OrderS(ArrayTypeS type, ImmutableList<ExprS> elems, Loc loc) implements ExprS {
  @Override
  public String name() {
    return "[]";
  }
}
