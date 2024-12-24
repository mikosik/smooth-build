package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasNameText;

public abstract sealed class PType implements HasNameText, HasLocation
    permits PExplicitType, PImplicitType {
  private final String nameText;
  private final Location location;

  protected PType(String nameText, Location location) {
    this.nameText = nameText;
    this.location = location;
  }

  @Override
  public String nameText() {
    return nameText;
  }

  public String q() {
    return Strings.q(nameText);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof PType that && this.nameText().equals(that.nameText());
  }

  @Override
  public int hashCode() {
    return nameText().hashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PType")
        .addField("nameText", nameText())
        .addField("location", location())
        .toString();
  }

  @Override
  public Location location() {
    return location;
  }
}
