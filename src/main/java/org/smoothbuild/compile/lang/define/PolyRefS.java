package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.SchemaS;

/**
 * Monomorphization of polymorphic referencable.
 */
public record PolyRefS(NamedEvaluableS namedEvaluable, Loc loc) implements PolyExprS {

  @Override
  public SchemaS schema() {
    return namedEvaluable.schema();
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "namedEvaluable = " + namedEvaluable,
        "loc = " + loc
    );
    return "PolyRefS(\n" + indent(fields) + "\n)";
  }
}
