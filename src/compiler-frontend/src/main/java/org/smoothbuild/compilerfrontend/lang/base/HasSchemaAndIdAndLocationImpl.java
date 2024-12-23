package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

public abstract class HasSchemaAndIdAndLocationImpl extends HasIdAndLocationImpl
    implements HasSchemaAndIdAndLocation {
  private final SSchema schema;

  public HasSchemaAndIdAndLocationImpl(SSchema schema, Id id, Location location) {
    super(id, location);
    this.schema = requireNonNull(schema);
  }

  @Override
  public SSchema schema() {
    return schema;
  }
}
