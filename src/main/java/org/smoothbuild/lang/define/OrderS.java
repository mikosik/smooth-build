package org.smoothbuild.lang.define;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.function.Function;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;

import com.google.common.collect.ImmutableList;

public record OrderS(ArrayTS type, ImmutableList<ExprS> elems, Loc loc) implements OperatorS {
  @Override
  public String name() {
    return "[]";
  }

  @Override
  public ExprS mapVars(Function<VarS, TypeS> mapper) {
    return new OrderS(type.mapVars(mapper), map(elems, a -> a.mapVars(mapper)), loc);
  }
}
