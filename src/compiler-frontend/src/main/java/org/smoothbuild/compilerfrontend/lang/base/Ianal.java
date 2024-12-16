package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.log.location.Location;

public class Ianal extends HasNameTextAndLocationImpl implements Ial {
  private Id id;

  public Ianal(String nameText, Location location) {
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
