package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.log.location.HasLocationImpl;
import org.smoothbuild.common.log.location.Location;

public class HasNameTextAndLocationImpl extends HasLocationImpl implements HasNameText {
  private final String nameText;

  public HasNameTextAndLocationImpl(String nameText, Location location) {
    super(location);
    this.nameText = nameText;
  }

  @Override
  public String nameText() {
    return nameText;
  }

  public String q() {
    return Strings.q(nameText);
  }
}
