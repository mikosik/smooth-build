package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import org.smoothbuild.compile.fs.lang.base.location.Location;

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
