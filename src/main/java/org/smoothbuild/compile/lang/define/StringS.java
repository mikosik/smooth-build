package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.qq;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.StringTS;

public record StringS(StringTS type, String string, Loc loc) implements ConstantS {
  @Override
  public String toString() {
    return "StringS(" + joinToString(", ", type, qq(string), loc) + ")";
  }
}
