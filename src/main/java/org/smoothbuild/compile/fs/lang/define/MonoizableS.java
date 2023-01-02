package org.smoothbuild.compile.fs.lang.define;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.SchemaS;

/**
 * Monomorphizable expression.
 */
public sealed interface MonoizableS
    permits AnonymousFuncS, RefS {
  public SchemaS schema();
  public Location location();
}
