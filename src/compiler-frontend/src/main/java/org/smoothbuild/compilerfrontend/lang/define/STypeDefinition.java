package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.lang.base.Tanal;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Type definition.
 */
public class STypeDefinition extends Tanal {
  public STypeDefinition(SType sType, Location location) {
    super(sType, sType.name(), location);
  }

  @Override
  public String toString() {
    var fields = list("type = " + type(), "location = " + location()).toString("\n");
    return "STypeDefinition(\n" + indent(fields) + "\n)";
  }
}
