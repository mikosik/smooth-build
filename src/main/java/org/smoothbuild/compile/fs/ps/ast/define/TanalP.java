package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.compile.fs.lang.base.NalImpl;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.TypeS;

/**
 * Mixin with TypeS, name and Location.
 */
public class TanalP extends NalImpl {
  private TypeS unifierType;

  public TanalP(String name, Location location) {
    super(name, location);
  }

  public TypeS unifierType() {
    return unifierType;
  }

  public void setUnifierType(TypeS unifierType) {
    this.unifierType = unifierType;
  }
}
