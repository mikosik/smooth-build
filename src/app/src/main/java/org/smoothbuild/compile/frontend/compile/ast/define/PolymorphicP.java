package org.smoothbuild.compile.frontend.compile.ast.define;

import org.smoothbuild.compile.frontend.lang.base.location.Located;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;

/**
 * Polymorphic entity.
 */
public abstract sealed class PolymorphicP implements Located
    permits LambdaP, ReferenceP {
  private final Location location;

  public PolymorphicP(Location location) {
    this.location = location;
  }

  @Override
  public Location location() {
    return location;
  }

  public abstract SchemaS schemaS();
}
