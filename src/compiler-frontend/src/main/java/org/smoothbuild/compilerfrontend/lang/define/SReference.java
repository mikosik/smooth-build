package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Reference to {@link SNamedEvaluable} or {@link SItem}.
 */
public record SReference(SSchema schema, Id referencedId, Location location)
    implements SPolymorphic, HasLocation {
  @Override
  public String toString() {
    var fields = list(
            "schema = " + schema, "referencedName = " + referencedId, "location = " + location)
        .toString("\n");
    return "SReference(\n" + indent(fields) + "\n)";
  }
}
