package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * SaIaL = Schema and Identifiable and Located.
 */
public abstract class Saial extends IalImpl {
  private final SSchema schema;

  public Saial(SSchema schema, Id id, Location location) {
    super(id, location);
    this.schema = requireNonNull(schema);
  }

  public SSchema schema() {
    return schema;
  }
}
