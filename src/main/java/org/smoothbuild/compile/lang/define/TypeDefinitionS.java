package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Tanal;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Type definition.
 */
public class TypeDefinitionS extends Tanal {
  public TypeDefinitionS(TypeS typeS, Location location) {
    super(typeS, typeS.name(), location);
  }
}
