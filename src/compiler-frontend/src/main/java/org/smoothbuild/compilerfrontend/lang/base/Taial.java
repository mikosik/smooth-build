package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;

/**
 * TaNaL = Type and Identifiable and Located.
 */
public abstract class Taial extends IalImpl {
  private final SType type;

  public Taial(SType type, Id id, Location location) {
    super(id, location);
    this.type = requireNonNull(type);
  }

  public SType type() {
    return type;
  }

  public SSchema schema() {
    return new SSchema(SVarSet.varSetS(), type);
  }
}
