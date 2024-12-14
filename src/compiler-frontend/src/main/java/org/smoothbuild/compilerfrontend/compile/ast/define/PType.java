package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Nal;

public abstract sealed class PType extends Nal permits PExplicitType, PImplicitType {
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
    var fields = list("nameText = " + nameText(), "location = " + location()).toString("\n");
    return "PType(\n" + indent(fields) + "\n)";
  }
}
