package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Tanal;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Type definition.
 */
public class TDefS extends Tanal {
  public TDefS(TypeS type, String name, Location location) {
    super(type, name, location);
  }
}
