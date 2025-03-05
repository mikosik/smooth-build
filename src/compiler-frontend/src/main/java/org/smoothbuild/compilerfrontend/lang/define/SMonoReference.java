package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Reference to {@link SMonoReferenceable}.
 */
public record SMonoReference(SType type, Id referencedId, Location location) implements SExpr {
  @Override
  public SType evaluationType() {
    return type;
  }

  @Override
  public String toSourceCode() {
    return referencedId.toString();
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SMonoReference")
        .addField("type", type)
        .addField("referencedName", referencedId)
        .addField("location", location)
        .toString();
  }
}
