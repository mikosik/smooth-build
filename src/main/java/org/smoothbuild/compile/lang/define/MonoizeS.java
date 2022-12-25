package org.smoothbuild.compile.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

/**
 * Monomorphization of monomorphizable expression.
 */
public record MonoizeS(
      ImmutableMap<VarS, TypeS> varMap,
      MonoizableS monoizableS,
      TypeS evalT,
      Location location)
    implements ExprS {

  public MonoizeS(MonoizableS monoizableS, Location location) {
    this(ImmutableMap.of(), monoizableS, location);
    checkArgument(monoizableS.schema().quantifiedVars().isEmpty());
  }

  public MonoizeS(ImmutableMap<VarS, TypeS> varMap, MonoizableS monoizableS, Location location) {
    this(varMap, monoizableS, monoizableS.schema().monoize(varMap), location);
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "varMap = " + varMap,
        "monoizableS = " + monoizableS,
        "evalT = " + evalT,
        "location = " + location
    );
    return "MonoizeS(\n" + indent(fields) + "\n)";
  }
}
