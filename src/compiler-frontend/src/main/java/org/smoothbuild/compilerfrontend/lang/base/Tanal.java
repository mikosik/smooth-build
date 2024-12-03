package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;

/**
 * TANAL = Type and Name and Loc.
 */
public abstract class Tanal extends NalImpl {
  private final SType type;

  public Tanal(SType type, String name, Location location) {
    super(name, location);
    this.type = requireNonNull(type);
  }

  public SType type() {
    return type;
  }

  public SSchema schema() {
    return new SSchema(SVarSet.varSetS(), type);
  }
}
