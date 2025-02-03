package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public final class PTypeReference extends PExplicitType {
  private Fqn fqn;

  public PTypeReference(String idText, Location location) {
    super(idText, location);
  }

  public Fqn fqn() {
    return fqn;
  }

  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }
}
