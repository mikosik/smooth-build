package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.STypeScheme;

/**
 * Reference to {@link SPolyEvaluable}.
 */
public record SPolyReference(STypeScheme scheme, Id referencedId, Location location)
    implements SReference {
  public String toSourceCode() {
    return referencedId.toString();
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SPolyReference")
        .addField("typeScheme", scheme)
        .addField("referencedName", referencedId)
        .addField("location", location)
        .toString();
  }
}
