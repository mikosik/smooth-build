package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.SchemaS;

/**
 * Monomorphization of polymorphic referencable.
 */
public record PolyRefS(
    PolyEvaluableS polyEvaluable,
    Loc loc) implements PolyExprS {

  @Override
  public SchemaS schema() {
    return polyEvaluable.schema();
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "polyEvaluable = " + polyEvaluable,
        "loc = " + loc
    );
    return "PolyRefS(\n" + indent(fields) + "\n)";
  }
}
