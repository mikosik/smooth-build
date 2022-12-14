package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.SchemaS;

/**
 * Monomorphizable expression.
 */
public sealed interface MonoizableS
    permits AnonymousFuncS, EvaluableRefS {
  public SchemaS schema();
  public Location location();
}
