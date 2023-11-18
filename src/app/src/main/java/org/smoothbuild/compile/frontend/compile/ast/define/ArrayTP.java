package org.smoothbuild.compile.frontend.compile.ast.define;

import org.smoothbuild.compile.frontend.lang.base.location.Location;

public final class ArrayTP extends ExplicitTP {
  private final TypeP elemT;

  public ArrayTP(TypeP elemT, Location location) {
    super("[" + elemT.name() + "]", location);
    this.elemT = elemT;
  }

  public TypeP elemT() {
    return elemT;
  }
}
