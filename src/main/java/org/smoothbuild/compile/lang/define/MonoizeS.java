package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Objects;
import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

public record MonoizeS(TypeS type, ImmutableMap<VarS, TypeS> varMap, PolyRefableS refable,
    Loc loc) implements OperS {
  @Override
  public String label() {
    return "<" + type + ">";
  }

  @Override
  public ExprS mapVars(Function<VarS, TypeS> mapper) {
    return new MonoizeS(type().mapVars(mapper), mapVarMap(mapper), refable, loc);
  }

  private ImmutableMap<VarS, TypeS> mapVarMap(Function<VarS, TypeS> mapper) {
    return mapValues(varMap, value -> ma(value, mapper));
  }

  private static TypeS ma(TypeS type, Function<VarS, TypeS> mapper) {
    if (type instanceof VarS var) {
      return mapper.apply(var);
    } else {
      return type;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MonoizeS monoizeS)) {
      return false;
    }
    return type.equals(monoizeS.type) && varMap.equals(monoizeS.varMap) && refable.equals(
        monoizeS.refable) && loc.equals(monoizeS.loc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, varMap, refable, loc);
  }
}
