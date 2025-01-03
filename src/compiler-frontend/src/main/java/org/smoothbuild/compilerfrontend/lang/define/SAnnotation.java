package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;

/**
 * Annotation.
 */
public record SAnnotation(String name, SString path, Location location) {
  public String toSourceCode() {
    return "@" + name + "(\"" + path.string() + "\")";
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SAnnotation")
        .addField("name", name)
        .addField("path", path)
        .addField("location", location)
        .toString();
  }
}
