package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.SchemaS;

/**
 * Reference to named evaluable.
 */
public record EvaluableRefS(NamedEvaluableS namedEvaluable, Location location) implements MonoizableS {

  @Override
  public SchemaS schema() {
    return namedEvaluable.schema();
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "namedEvaluable = " + namedEvaluable,
        "location = " + location
    );
    return "EvaluableRefS(\n" + indent(fields) + "\n)";
  }
}
