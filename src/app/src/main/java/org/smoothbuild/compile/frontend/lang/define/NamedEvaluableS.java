package org.smoothbuild.compile.frontend.lang.define;

import org.smoothbuild.compile.frontend.lang.base.Nal;
import org.smoothbuild.compile.frontend.lang.base.Sanal;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;

/**
 * Evaluable that has fully qualified name.
 */
public abstract sealed class NamedEvaluableS extends Sanal
    implements EvaluableS, ReferenceableS, Nal permits NamedFuncS, NamedValueS {
  public NamedEvaluableS(SchemaS schema, String name, Location location) {
    super(schema, name, location);
  }
}
