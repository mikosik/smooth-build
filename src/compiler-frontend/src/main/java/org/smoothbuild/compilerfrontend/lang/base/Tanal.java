package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;
import org.smoothbuild.compilerfrontend.lang.type.VarSetS;

/**
 * TANAL = Type and Name and Loc.
 */
public abstract class Tanal extends NalImpl {
  private final TypeS type;

  public Tanal(TypeS type, String name, Location location) {
    super(name, location);
    this.type = requireNonNull(type);
  }

  public TypeS type() {
    return type;
  }

  public SchemaS schema() {
    return new SchemaS(VarSetS.varSetS(), type);
  }
}
