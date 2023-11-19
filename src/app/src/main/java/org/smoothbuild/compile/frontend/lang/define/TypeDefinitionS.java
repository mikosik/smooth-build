package org.smoothbuild.compile.frontend.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import org.smoothbuild.compile.frontend.lang.base.Tanal;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.TypeS;

/**
 * Type definition.
 */
public class TypeDefinitionS extends Tanal {
  public TypeDefinitionS(TypeS typeS, Location location) {
    super(typeS, typeS.name(), location);
  }

  @Override
  public String toString() {
    var fields = joinToString("\n", "type = " + type(), "location = " + location());
    return "TypeDefinitionS(\n" + indent(fields) + "\n)";
  }
}
