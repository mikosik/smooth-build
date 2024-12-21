package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasNameTextAndLocationImpl;

public abstract sealed class PType extends HasNameTextAndLocationImpl
    permits PExplicitType, PImplicitType {
  protected PType(String nameText, Location location) {
    super(nameText, location);
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
}
