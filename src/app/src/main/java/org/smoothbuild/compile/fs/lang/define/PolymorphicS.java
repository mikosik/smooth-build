package org.smoothbuild.compile.fs.lang.define;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.SchemaS;

/**
 * Polymorphic entity.
 */
public sealed interface PolymorphicS
    permits AnonymousFuncS, ReferenceS {
  public SchemaS schema();
  public Location location();
}
