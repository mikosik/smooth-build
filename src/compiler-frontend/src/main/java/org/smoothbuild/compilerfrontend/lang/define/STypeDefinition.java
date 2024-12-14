package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.base.Taial;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Type definition.
 */
public class STypeDefinition extends Taial {
  public STypeDefinition(Id id, SType sType, Location location) {
    super(sType, id, location);
  }

  @Override
  public String toString() {
    var fields = list("type = " + type(), "location = " + location()).toString("\n");
    return "STypeDefinition(\n" + indent(fields) + "\n)";
  }
}
