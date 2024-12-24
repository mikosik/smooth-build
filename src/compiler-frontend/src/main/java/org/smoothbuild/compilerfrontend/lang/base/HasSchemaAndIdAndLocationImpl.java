package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

public abstract class HasSchemaAndIdAndLocationImpl
    implements HasSchemaAndIdAndLocation, HasIdAndLocation, HasLocation {
  private final SSchema schema;
  private final Id id;
  private final Location location;

  public HasSchemaAndIdAndLocationImpl(SSchema schema, Id id, Location location) {
    this.id = id;
    this.location = location;
    this.schema = requireNonNull(schema);
  }

  @Override
  public SSchema schema() {
    return schema;
  }

  @Override
  public Id id() {
    return id;
  }

  @Override
  public Location location() {
    return location;
  }
}
