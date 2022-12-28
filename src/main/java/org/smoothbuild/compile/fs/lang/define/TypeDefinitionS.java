package org.smoothbuild.compile.fs.lang.define;

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
}
