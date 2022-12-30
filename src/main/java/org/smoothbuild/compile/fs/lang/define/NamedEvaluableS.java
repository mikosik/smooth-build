package org.smoothbuild.compile.fs.lang.define;

import org.smoothbuild.compile.fs.lang.base.Nal;
import org.smoothbuild.compile.fs.lang.base.Sanal;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.SchemaS;

/**
 * Evaluable that has fully qualified name.
 */
public sealed abstract class NamedEvaluableS
    extends Sanal
    implements EvaluableS, RefableS, Nal
    permits NamedFuncS, NamedValueS {
  public NamedEvaluableS(SchemaS schema, String name, Location location) {
    super(schema, name, location);
  }
}
