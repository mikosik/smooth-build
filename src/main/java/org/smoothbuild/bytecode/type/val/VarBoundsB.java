package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.lang.type.api.Bounded;
import org.smoothbuild.lang.type.api.Var;
import org.smoothbuild.lang.type.api.VarBounds;

import com.google.common.collect.ImmutableMap;

public record VarBoundsB(ImmutableMap<Var, Bounded<TypeB>> map) implements VarBounds<TypeB> {
  private static final VarBoundsB EMPTY = new VarBoundsB(ImmutableMap.of());

  public static VarBoundsB varBoundsB() {
    return EMPTY;
  }

  public static VarBoundsB varBoundsB(Bounded<TypeB> bounded) {
    return new VarBoundsB(ImmutableMap.of(bounded.var(), bounded));
  }

  @Override
  public String toString() {
    return toCommaSeparatedString(map.values());
  }
}
