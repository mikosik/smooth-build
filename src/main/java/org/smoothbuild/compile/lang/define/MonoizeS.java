package org.smoothbuild.compile.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

/**
 * Monomorphization of monomorphizable expression.
 */
public record MonoizeS(ImmutableMap<VarS, TypeS> varMap, MonoizableS monoizableS, TypeS evalT, Loc loc)
    implements ExprS {

  public MonoizeS(MonoizableS monoizableS, Loc loc) {
    this(ImmutableMap.of(), monoizableS, loc);
    checkArgument(monoizableS.schema().quantifiedVars().isEmpty());
  }

  public MonoizeS(ImmutableMap<VarS, TypeS> varMap, MonoizableS monoizableS, Loc loc) {
    this(varMap, monoizableS, monoizableS.schema().monoize(varMap), loc);
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "varMap = " + varMap,
        "monoizableS = " + monoizableS,
        "evalT = " + evalT,
        "loc = " + loc
    );
    return "MonoizeS(\n" + indent(fields) + "\n)";
  }
}
