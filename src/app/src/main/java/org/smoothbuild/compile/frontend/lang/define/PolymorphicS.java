package org.smoothbuild.compile.frontend.lang.define;

import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;

/**
 * Polymorphic entity.
 */
public sealed interface PolymorphicS
    permits LambdaS, ReferenceS {
  public SchemaS schema();
  public Location location();
}
