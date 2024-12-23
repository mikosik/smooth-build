package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;

public class HasIdAndNameTextAndLocation extends HasNameTextAndLocationImpl
    implements HasIdAndLocation {
  private Id id;

  public HasIdAndNameTextAndLocation(String nameText, Location location) {
    super(nameText, location);
  }

  public void setId(Id id) {
    this.id = id;
  }

  @Override
  public Id id() {
    return id;
  }
}
