package org.smoothbuild.compile.frontend.lang.define;

import static org.smoothbuild.common.Strings.qq;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.StringTS;

public record StringS(StringTS type, String string, Location location) implements ConstantS {
  @Override
  public String toString() {
    return "StringS(" + joinToString(", ", type, qq(string), location) + ")";
  }
}