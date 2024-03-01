package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.location.Location;

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
