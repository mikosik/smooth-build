package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

/**
 * Monomorphization of polymorphic referencable.
 */
public record PolyRefS(
    ImmutableMap<VarS, TypeS> varMap,
    PolyEvaluableS polyEvaluable,
    TypeS evalT,
    Loc loc) implements OperS {

  public PolyRefS(PolyEvaluableS polyEvaluable, Loc loc) {
    this(ImmutableMap.of(), polyEvaluable, loc);
  }

  public PolyRefS(ImmutableMap<VarS, TypeS> varMap, PolyEvaluableS polyEvaluable, Loc loc) {
    this(varMap, polyEvaluable, polyEvaluable.schema().monoize(varMap), loc);
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "varMap = " + varMap,
        "polyEvaluable = " + polyEvaluable,
        "evalT = " + evalT,
        "loc = " + loc
    );
    return "PolyRefS(\n" + indent(fields) + "\n)";
  }
}
