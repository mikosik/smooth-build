package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.qq;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SStringType;

public record SString(SStringType type, String string, Location location) implements SConstant {
  @Override
  public String toString() {
    return "SString(" + list(type, qq(string), location).toString(", ") + ")";
  }
}
