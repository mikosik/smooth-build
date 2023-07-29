package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import org.smoothbuild.compile.fs.lang.base.Nal;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.SchemaS;

/**
 * Reference to {@link NamedEvaluableS} or {@link ItemS}.
 */
public record ReferenceS(SchemaS schema, String name, Location location)
    implements PolymorphicS, Nal {
  @Override
  public String toString() {
    var fields = joinToString("\n",
        "schema = " + schema,
        "name = " + name,
        "location = " + location
    );
    return "ReferenceS(\n" + indent(fields) + "\n)";
  }
}
