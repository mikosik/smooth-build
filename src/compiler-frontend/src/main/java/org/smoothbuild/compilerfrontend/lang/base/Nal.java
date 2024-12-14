package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.log.location.Located;
import org.smoothbuild.common.log.location.Location;

public class Nal implements Located {
  private final String nameText;
  private final Location location;

  public Nal(String nameText, Location location) {
    this.nameText = nameText;
    this.location = location;
  }

  public String nameText() {
    return nameText;
  }

  public String q() {
    return Strings.q(nameText);
  }

  @Override
  public Location location() {
    return location;
  }
}
