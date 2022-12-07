package org.smoothbuild.compile.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

/**
 * Monomorphization of expression.
 */
public record MonoizeS(ImmutableMap<VarS, TypeS> varMap, PolyExprS polyExprS, TypeS evalT, Loc loc)
    implements ExprS {

  public MonoizeS(PolyExprS polyExprS, Loc loc) {
    this(ImmutableMap.of(), polyExprS, loc);
    checkArgument(polyExprS.schema().quantifiedVars().isEmpty());
  }

  public MonoizeS(ImmutableMap<VarS, TypeS> varMap, PolyExprS polyExprS, Loc loc) {
    this(varMap, polyExprS, polyExprS.schema().monoize(varMap), loc);
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "varMap = " + varMap,
        "polyExprS = " + polyExprS,
        "evalT = " + evalT,
        "loc = " + loc
    );
    return "MonoizeS(\n" + indent(fields) + "\n)";
  }
}
