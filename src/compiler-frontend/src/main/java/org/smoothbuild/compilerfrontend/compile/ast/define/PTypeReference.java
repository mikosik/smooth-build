package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;

public final class PTypeReference extends PExplicitType {
  private Id id;

  public PTypeReference(String idText, Location location) {
    super(idText, location);
  }

  public Id id() {
    return id;
  }

  public void setId(Id id) {
    this.id = id;
  }
}
