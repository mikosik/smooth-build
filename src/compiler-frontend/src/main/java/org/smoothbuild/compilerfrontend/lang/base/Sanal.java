package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * SANAL = Schema and Name and Loc.
 */
public abstract class Sanal extends NalImpl {
  private final SSchema schema;

  public Sanal(SSchema schema, String name, Location location) {
    super(name, location);
    this.schema = requireNonNull(schema);
  }

  public SSchema schema() {
    return schema;
  }
}
