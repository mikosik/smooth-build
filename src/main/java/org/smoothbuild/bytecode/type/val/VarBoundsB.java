package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import com.google.common.collect.ImmutableMap;

public record VarBoundsB(ImmutableMap<VarB, BoundedB> map) {
  private static final VarBoundsB EMPTY = new VarBoundsB(ImmutableMap.of());

  public static VarBoundsB varBoundsB() {
    return EMPTY;
  }

  public static VarBoundsB varBoundsB(BoundedB bounded) {
    return new VarBoundsB(ImmutableMap.of(bounded.var(), bounded));
  }

  @Override
  public String toString() {
    return toCommaSeparatedString(map.values());
  }
}
