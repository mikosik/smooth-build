package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.NalImpl;

public abstract sealed class PType extends NalImpl permits PExplicitType, PImplicitType {
  protected PType(String name, Location location) {
    super(name, location);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof PType that && this.name().equals(that.name());
  }

  @Override
  public int hashCode() {
    return name().hashCode();
  }

  @Override
  public String toString() {
    var fields = list("name = " + name(), "location = " + location()).toString("\n");
    return "PType(\n" + indent(fields) + "\n)";
  }
}
