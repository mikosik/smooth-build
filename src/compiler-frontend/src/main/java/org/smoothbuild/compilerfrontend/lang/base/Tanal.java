package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

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

  public SchemaS schema() {
    return new SchemaS(SVarSet.varSetS(), type);
  }
}
