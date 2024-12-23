package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Reference to {@link SNamedEvaluable} or {@link SItem}.
 */
public record SReference(SSchema schema, Id referencedId, Location location)
    implements SPolymorphic, HasLocation {
  @Override
  public String toString() {
    return new ToStringBuilder("SReference")
        .addField("schema", schema)
        .addField("referencedName", referencedId)
        .addField("location", location)
        .toString();
  }
}
