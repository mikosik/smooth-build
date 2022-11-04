package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;

/**
 * Annotation.
 */
public record AnnS(String name, StringS path, Loc loc) {
  @Override
  public String toString() {
    var fields = joinToString("\n",
        "name = " + name,
        "path = " + path,
        "loc = " + loc);
    return "AnnS(\n" + indent(fields) + "\n)";
  }
}
