package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

public abstract class HasSchemaAndIdAndLocation extends HasIdAndLocationImpl {
  private final SSchema schema;

  public HasSchemaAndIdAndLocation(SSchema schema, Id id, Location location) {
    super(id, location);
    this.schema = requireNonNull(schema);
  }

  public SSchema schema() {
    return schema;
  }
}
