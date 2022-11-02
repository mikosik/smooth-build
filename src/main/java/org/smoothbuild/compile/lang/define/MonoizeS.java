package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

/**
 * Monomorphization of polymorphic referencable.
 */
public record MonoizeS(ImmutableMap<VarS, TypeS> varMap, PolyRefS polyRef, TypeS evalT, Loc loc)
    implements OperS {

  public MonoizeS(ImmutableMap<VarS, TypeS> varMap, PolyRefS polyRefS, Loc loc) {
    this(varMap, polyRefS, polyRefS.polyEvaluable().schema().monoize(varMap), loc);
  }
}
