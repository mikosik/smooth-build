package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasTypeAndIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Type definition.
 */
public class STypeDefinition extends HasTypeAndIdAndLocation {
  public STypeDefinition(Id id, SType sType, Location location) {
    super(sType, id, location);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("STypeDefinition")
        .addField("type", type())
        .addField("id", id())
        .addField("location", location())
        .toString();
  }
}
