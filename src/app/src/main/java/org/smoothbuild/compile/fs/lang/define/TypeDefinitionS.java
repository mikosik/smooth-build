package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import org.smoothbuild.compile.fs.lang.base.Tanal;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.TypeS;

/**
 * Type definition.
 */
public class TypeDefinitionS extends Tanal {
  public TypeDefinitionS(TypeS typeS, Location location) {
    super(typeS, typeS.name(), location);
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "type = " + type(),
        "location = " + location()
    );
    return "TypeDefinitionS(\n" + indent(fields) + "\n)";
  }
}
