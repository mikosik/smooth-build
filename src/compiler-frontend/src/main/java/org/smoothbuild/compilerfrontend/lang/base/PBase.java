package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.log.location.Location;

public class PBase implements Ial {
  private final String nameText;
  private Id id;
  private final Location location;

  public PBase(String nameText, Location location) {
    this.nameText = nameText;
    this.location = location;
  }

  public String nameText() {
    return nameText;
  }

  public void setId(Id id) {
    this.id = id;
  }

  @Override
  public Id id() {
    return id;
  }

  @Override
  public Location location() {
    return location;
  }
}
