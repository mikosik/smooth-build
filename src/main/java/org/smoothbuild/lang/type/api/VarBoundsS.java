package org.smoothbuild.lang.type.api;

import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.lang.type.impl.TypeS;

import com.google.common.collect.ImmutableMap;

public record VarBoundsS(ImmutableMap<Var, Bounded<TypeS>> map) implements VarBounds<TypeS> {
  private static final VarBoundsS EMPTY = new VarBoundsS(ImmutableMap.of());

  public static VarBoundsS varBoundsS() {
    return EMPTY;
  }

  public static VarBoundsS varBoundsS(Bounded<TypeS> bounded) {
    return new VarBoundsS(ImmutableMap.of(bounded.var(), bounded));
  }

  @Override
  public String toString() {
    return toCommaSeparatedString(map.values());
  }
}
