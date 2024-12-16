package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.log.location.HasLocationImpl;
import org.smoothbuild.common.log.location.Location;

public class Nal extends HasLocationImpl {
  private final String nameText;

  public Nal(String nameText, Location location) {
    super(location);
    this.nameText = nameText;
  }

  public String nameText() {
    return nameText;
  }

  public String q() {
    return Strings.q(nameText);
  }
}
