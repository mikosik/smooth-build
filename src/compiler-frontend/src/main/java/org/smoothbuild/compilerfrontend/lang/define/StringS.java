package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.Strings.qq;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.StringTS;

public record StringS(StringTS type, String string, Location location) implements ConstantS {
  @Override
  public String toString() {
    return "StringS(" + list(type, qq(string), location).toString(", ") + ")";
  }
}
