package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.TypeDefinition;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Type definition.
 */
public class STypeDefinition implements TypeDefinition {
  private final SType type;
  private final Id id;
  private final Location location;

  public STypeDefinition(SType type, Id id, Location location) {
    this.type = type;
    this.id = id;
    this.location = location;
  }

  @Override
  public SType type() {
    return type;
  }

  @Override
  public Id id() {
    return id;
  }

  @Override
  public Location location() {
    return location;
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
