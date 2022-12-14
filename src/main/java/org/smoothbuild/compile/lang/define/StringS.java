package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.qq;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.StringTS;

public record StringS(StringTS type, String string, Location location) implements ConstantS {
  @Override
  public String toString() {
    return "StringS(" + joinToString(", ", type, qq(string), location) + ")";
  }
}
