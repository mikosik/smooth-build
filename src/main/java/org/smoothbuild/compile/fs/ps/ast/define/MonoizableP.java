package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.compile.fs.lang.base.location.Located;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.SchemaS;

/**
 * Entity that can be monomorphized.
 */
public abstract sealed class MonoizableP implements Located
    permits AnonymousFuncP, ReferenceP {
  private final Location location;

  public MonoizableP(Location location) {
    this.location = location;
  }

  @Override
  public Location location() {
    return location;
  }

  public abstract SchemaS schemaS();
}
