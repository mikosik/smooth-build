package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.impl.ArrayTS;

import com.google.common.collect.ImmutableList;

public record OrderS(ArrayTS type, ImmutableList<ExprS> elems, Loc loc) implements ExprS {
  public OrderS {
    checkArgument(!type.hasOpenVars());
  }

  @Override
  public String name() {
    return "[]";
  }
}
