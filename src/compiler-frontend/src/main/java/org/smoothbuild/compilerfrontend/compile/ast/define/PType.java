package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.HasLocationImpl;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasNameText;

public abstract sealed class PType extends HasLocationImpl implements HasNameText
    permits PExplicitType, PImplicitType {
  private final String nameText;

  protected PType(String nameText, Location location) {
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
