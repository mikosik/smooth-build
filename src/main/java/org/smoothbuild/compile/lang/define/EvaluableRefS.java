package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.SchemaS;

/**
 * Reference to named evaluable.
 */
public record EvaluableRefS(NamedEvaluableS namedEvaluable, Loc loc) implements MonoizableS {

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
    return "EvaluableRefS(\n" + indent(fields) + "\n)";
  }
}
