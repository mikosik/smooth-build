package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.compile.fs.lang.base.location.Location;

public final class ArrayTP extends TypeP {
  private final TypeP elemT;

  public ArrayTP(TypeP elemT, Location location) {
    super("[" + elemT.name() + "]", location);
    this.elemT = elemT;
  }

  public TypeP elemT() {
    return elemT;
  }
}
