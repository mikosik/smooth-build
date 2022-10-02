package org.smoothbuild.compile.lang.define;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

/**
 * Monomorphization of polymorphic referencable.
 */
public record MonoizeS(TypeS type, ImmutableMap<VarS, TypeS> varMap, PolyEvaluableS polyEvaluable,
    Loc loc) implements OperS {
  @Override
  public String label() {
    return "<" + type + ">";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return o instanceof MonoizeS monoizeS
        && type.equals(monoizeS.type)
        && varMap.equals(monoizeS.varMap)
        && polyEvaluable.equals(monoizeS.polyEvaluable)
        && loc.equals(monoizeS.loc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, varMap, polyEvaluable, loc);
  }
}
