package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.lang.base.location.Located;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

/**
 * Reference to {@link SNamedEvaluable} or {@link SItem}.
 */
public record SReference(SchemaS schema, String referencedName, Location location)
    implements SPolymorphic, Located {
  @Override
  public String toString() {
    var fields = list(
            "schema = " + schema, "referencedName = " + referencedName, "location = " + location)
        .toString("\n");
    return "SReference(\n" + indent(fields) + "\n)";
  }
}
