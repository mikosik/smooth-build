package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.base.Sanal;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

/**
 * Evaluable that has fully qualified name.
 */
public abstract sealed class NamedEvaluableS extends Sanal
    implements EvaluableS, ReferenceableS, Nal permits NamedFuncS, NamedValueS {
  public NamedEvaluableS(SchemaS schema, String name, Location location) {
    super(schema, name, location);
  }
}
