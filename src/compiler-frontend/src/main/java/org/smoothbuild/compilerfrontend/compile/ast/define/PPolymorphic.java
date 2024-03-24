package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.location.Located;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

/**
 * Polymorphic entity.
 */
public abstract sealed class PPolymorphic implements Located permits PLambda, PReference {
  private final Location location;

  public PPolymorphic(Location location) {
    this.location = location;
  }

  @Override
  public Location location() {
    return location;
  }

  public abstract SchemaS schemaS();
}
