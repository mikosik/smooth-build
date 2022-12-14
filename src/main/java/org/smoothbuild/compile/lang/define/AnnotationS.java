package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.location.Location;

/**
 * Annotation.
 */
public record AnnotationS(String name, StringS path, Location location) {
  @Override
  public String toString() {
    var fields = joinToString("\n",
        "name = " + name,
        "path = " + path,
        "location = " + location);
    return "AnnotationS(\n" + indent(fields) + "\n)";
  }
}
