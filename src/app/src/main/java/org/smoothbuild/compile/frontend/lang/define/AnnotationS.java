package org.smoothbuild.compile.frontend.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compile.frontend.lang.base.location.Location;

/**
 * Annotation.
 */
public record AnnotationS(String name, StringS path, Location location) {
  @Override
  public String toString() {
    var fields =
        list("name = " + name, "path = " + path, "location = " + location).toString("\n");
    return "AnnotationS(\n" + indent(fields) + "\n)";
  }
}
