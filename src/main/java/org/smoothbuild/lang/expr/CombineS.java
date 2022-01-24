package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.StructTS;

import com.google.common.collect.ImmutableList;

public record CombineS(StructTS type, ImmutableList<ExprS> elems, Loc loc) implements ExprS {
  public CombineS {
    checkArgument(!type.hasOpenVars());
  }

  @Override
  public String name() {
    return "{}";
  }
}
