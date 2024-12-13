package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.log.location.Located;
import org.smoothbuild.common.log.location.Location;

public abstract sealed class PType implements Located permits PExplicitType, PImplicitType {
  private final String nameText;
  private final Location location;

  protected PType(String nameText, Location location) {
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
    var fields = list("nameText = " + nameText(), "location = " + location()).toString("\n");
    return "PType(\n" + indent(fields) + "\n)";
  }
}
