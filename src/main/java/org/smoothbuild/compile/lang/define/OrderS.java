package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableList;

public record OrderS(ArrayTS type, ImmutableList<ExprS> elems, Loc loc) implements OperS {
  @Override
  public String label() {
    return "[]";
  }

  @Override
  public ExprS mapVars(Function<VarS, TypeS> mapper) {
    return new OrderS(type.mapVars(mapper), map(elems, a -> a.mapVars(mapper)), loc);
  }
}
