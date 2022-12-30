package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.SchemaS;

/**
 * Reference to named evaluable.
 */
public record EvaluableRefS(SchemaS schema, String name, Location location) implements MonoizableS {
  @Override
  public String toString() {
    var fields = joinToString("\n",
        "schema = " + schema,
        "name = " + name,
        "location = " + location
    );
    return "EvaluableRefS(\n" + indent(fields) + "\n)";
  }
}
