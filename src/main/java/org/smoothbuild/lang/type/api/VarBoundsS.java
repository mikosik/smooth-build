package org.smoothbuild.lang.type.api;

import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.lang.type.impl.BoundedS;
import org.smoothbuild.lang.type.impl.VarS;

import com.google.common.collect.ImmutableMap;

public record VarBoundsS(ImmutableMap<VarS, BoundedS> map) {
  private static final VarBoundsS EMPTY = new VarBoundsS(ImmutableMap.of());

  public static VarBoundsS varBoundsS() {
    return EMPTY;
  }

  public static VarBoundsS varBoundsS(BoundedS bounded) {
    return new VarBoundsS(ImmutableMap.of(bounded.var(), bounded));
  }

  @Override
  public String toString() {
    return toCommaSeparatedString(map.values());
  }
}
