package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Reference to {@link SMonoReferenceable}.
 */
public record SMonoReference(SSchema schema, Id referencedId, Location location)
    implements SReference {
  @Override
  public String toSourceCode() {
    return referencedId.toString();
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SMonoReference")
        .addField("schema", schema)
        .addField("referencedName", referencedId)
        .addField("location", location)
        .toString();
  }
}
