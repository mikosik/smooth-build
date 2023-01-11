package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.compile.fs.lang.base.location.Located;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.TypeS;

/**
 * Mixin with TypeS and Location.
 */
public class TalP implements Located {
  private final Location location;
  private TypeS unifierType;

  public TalP(Location location) {
    this.location = location;
  }

  @Override
  public Location location() {
    return location;
  }

  public TypeS unifierType() {
    return unifierType;
  }

  public void setUnifierType(TypeS unifierType) {
    this.unifierType = unifierType;
  }
}
