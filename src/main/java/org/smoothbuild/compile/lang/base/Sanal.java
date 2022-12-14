package org.smoothbuild.compile.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.SchemaS;

/**
 * SANAL = Schema and Name and Loc.
 */
public abstract class Sanal extends NalImpl {
  private final SchemaS schema;

  public Sanal(SchemaS schema, String name, Location location) {
    super(name, location);
    this.schema = requireNonNull(schema);
  }

  public SchemaS schema() {
    return schema;
  }
}
