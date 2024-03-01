package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

/**
 * Reference to {@link NamedEvaluableS} or {@link ItemS}.
 */
public record ReferenceS(SchemaS schema, String name, Location location)
    implements PolymorphicS, Nal {
  @Override
  public String toString() {
    var fields =
        list("schema = " + schema, "name = " + name, "location = " + location).toString("\n");
    return "ReferenceS(\n" + indent(fields) + "\n)";
  }
}
