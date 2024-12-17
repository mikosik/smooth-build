package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.base.Id;

public final class PIdType extends PExplicitType implements HasIdAndLocation {
  private Id id;

  public PIdType(String idText, Location location) {
    super(idText, location);
  }

  @Override
  public Id id() {
    return id;
  }

  public void setId(Id id) {
    this.id = id;
  }
}
